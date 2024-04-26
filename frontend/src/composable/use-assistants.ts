/*
 * Copyright (c) 2024 Jean Schmitz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This module provides a set of functions to manage and interact with chat assistants.
 * It includes functions to handle streaming, synchronization, selection, retrieval, deletion, and creation of assistants.
 * It also includes functions to manage threads and messages, including retrieval, creation, deletion, and updating.
 * Additionally, it provides utility functions for handling assistant images and prompts.
 *
 * @module useAssistants
 */
import debounce from 'lodash/debounce';
import {useChatStore} from '@/store/chat-store';
import {onMounted, onUnmounted} from 'vue';
import {ParsedThreadMessage, ThreadMessage} from '@/store/to/thread';
import axios from 'axios';
import Assistant from '@/store/to/assistant';
import {useHighlighting} from '@/composable/use-highligthing';

const DELAY_TIME = 20;
const DEBOUNCE_TIME = 200;
const DEBOUNCE_MAXWAIT = 1000;

enum Order {
  ASCENDING = 'ASCENDING',
  DESCENDING = 'DESCENDING',
}

export function useAssistants() {
  const chatStore = useChatStore();
  const {replaceCodeContent} = useHighlighting();

  onMounted(() => {

  });

  onUnmounted(() => {

  });

  const streamRun = async (
      assistantId: string, threadId: string, message: string, chunkUpdateCallback: () => void) => {

    const debouncedUpdateCallback = debounce(chunkUpdateCallback, DEBOUNCE_TIME,
        {maxWait: DEBOUNCE_MAXWAIT});

    chunkUpdateCallback();

    chatStore.runId = '';
    try {
      const response = await fetchSSE(assistantId, threadId, message);
      const reader = response.body?.getReader();
      if (!reader) return;

      const decoder = new TextDecoder('utf-8');
      let partial = '';

      let isReading = true;
      while (isReading) {
        const chunk = await reader.read();
        const chunkValue = decoder.decode(chunk.value, {stream: true});
        console.log('CHUNK: ', chunkValue);

        if (chunk.done) {
          await postStreamProcessing(threadId);
          chatStore.removeStatus();
          isReading = false;
        } else {
          partial += chunkValue;
          const parts = partial.split('\n');
          partial = parts.pop() ?? '';

          let currentEvent = '';
          for (const part of parts) {
            if (part.startsWith('event:')) {
              currentEvent = part.substring(6);
            } else if (part.startsWith('data:')) {
              const data = part.substring(5);
              processData(data, currentEvent, debouncedUpdateCallback);
              currentEvent = '';
              await sleep(DELAY_TIME);
            }
          }
        }
      }
    } finally {
      chatStore.runId = '';
    }
  };

  const sleep = async (delay: number) => {
    return new Promise((resolve) => setTimeout(resolve, delay));
  };

  const fetchSSE = async (assistantId: string, threadId: string, message: string) => {
    return await fetch(`/api/v1/threads/${threadId}/runs/stream`, {
      method: 'POST',
      cache: 'no-cache',
      keepalive: true,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'text/event-stream',
      },
      body: JSON.stringify({
        assistantId,
        message,
      }),
    });
  };

  const hasJSONData = (data: string): boolean => {
    return data.indexOf('{') != -1;
  };

  const escapeHtml = (html: string): string => {
    const tagsToReplace: { [key: string]: string } = {
      '<': '&lt;',
      '>': '&gt;',
    };

    return html.replace(/[<>]/g, (tag: string) => tagsToReplace[tag] || tag);
  };

  const postStreamProcessing = async (threadId: string) => {
    const processedMessage = await postprocessLastMessage(threadId);

    const message = processedMessage.message;
    const messageId = message?.id ?? '';
    const assistantId = message?.assistantId ?? '';

    if (processedMessage?.parsedContent) {
      const codeContent = replaceCodeContent(processedMessage.parsedContent);

      const newMessage = new ThreadMessage(messageId, 'assistant', codeContent, assistantId);
      newMessage.threadId = threadId;

      chatStore.threadMessages.pop();
      chatStore.threadMessages.push(newMessage);
    }

  };

  const postprocessLastMessage = async (threadId: string): Promise<ParsedThreadMessage> => {
    const result = await axios.post(
        `/api/v1/threads/${threadId}/messages/last/postprocess`,
        {},
    );
    return result.data;
  };

  const processData = (data: string, event: string, debouncedUpdateCallback: () => void) => {
    console.log('PROCESS event=' + event + ': ', data);
    //if (!hasJSONData(data)) return;

    switch (event) {
      case 'thread.message.delta':
        processDeltaEvent(data);
        break;
      case 'thread.run.created':
        processRunCreatedEvent(data);
        break;
    }

    debouncedUpdateCallback();
  };

  const processRunCreatedEvent = (data: string) => {
    console.log('## processRunCreatedEvent', data);
    chatStore.runId = JSON.parse(data)['id'];
  };

  const processDeltaEvent = (data: string) => {
    console.log('## processDeltaEvent', data);
    const lastMessage = chatStore.getLastMessage();

    if (data.length > 0) {
      let newContent = escapeHtml(data);
      newContent = newContent.replaceAll(/\\n/g, '<br/>');
      lastMessage.content += newContent;
    }
  };

  const retrieveGPTModels = async () => {
    console.log('Retrieving GPT models');
    const result = await axios.get('/api/v1/assistants/models');
    return result.data;
  };

  // const syncAssistants = async () => {
  //   console.log('Syncing assistants');
  //   await axios.post('/api/v1/assistants/sync');
  //   await retrieveAssistants();
  // };

  const retrieveAssistants = async () => {
    console.log('Retrieving assistants');
    const result = await axios.get('/api/v1/assistants', {
      params: {
        order: Order.ASCENDING,
      },
    });
    chatStore.assistantList = result.data;
  };

  const retrieveAssistant = async (assistantId: string): Promise<Assistant> => {
    console.log('Retrieving assistant with id:', assistantId);
    const result = await axios.get(`/api/v1/assistants/${assistantId}`);
    return result.data;
  };

  const selectAssistant = async (assistantId: string) => {
    chatStore.selectedAssistant = await retrieveAssistant(assistantId);
  };

  const retrieveThreads = async () => {
    const result = await axios.get(
        `/api/v1/threads`,
        {
          params: {},
        },
    );

    chatStore.threads = result.data;
  };

  const retrieveThread = async (threadId: string) => {
    const result = await axios.get(
        `/api/v1/threads/${threadId}`,
    );
    return result.data;
  };

  const createThread = async () => {
    const result = await axios.post(`/api/v1/threads`);
    return result.data;
  };

  const getThreadMessageTextContent = (threadMessage: ThreadMessage | undefined): string => {
    if (!threadMessage) return '';

    if (threadMessage.content && threadMessage.content.length > 0 &&
        threadMessage.content) {
      return threadMessage.content ?? '';
    }
    return '';
  };

  const uploadAssistantImage = async (file: any) => {
    const formData = new FormData();
    formData.append('file', file);

    return await axios.post('/api/v1/assistants/images/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  };

  const generateAssistantImage = async (prompt: string) => {
    return await axios.post(`/api/v1/assistants/images/generate`, {prompt});
  };

  const postprocessMessage = async (
      threadId: string, messageId: string): Promise<ParsedThreadMessage> => {
    const result = await axios.post(
        `/api/v1/threads/${threadId}/messages/${messageId}/postprocess`,
        {},
    );
    return result.data;
  };

  const cancelRun = async (threadId: string, id: string) => {
    return axios.post(`/api/v1/threads/${threadId}/runs/${id}/cancel`);
  };

  const regenerateRun = async (threadId: any, id: any) => {
    return axios.post(`/api/v1/threads/${threadId}/runs/${id}/regenerate`);
  };

  const getAssistantById = (assistantId: string) => {
    return chatStore.assistantList.find((assistant) => assistant.id === assistantId);
  };

  const deleteAssistant = async (assistantId: string) => {
    console.log('Delete assistant with id:', assistantId);
    const result = await axios.delete(`/api/v1/assistants/${assistantId}`);
    return result.data;
  };

  const modifyAssistant = async (assistantId: string, assistant: Assistant) => {
    console.log('Modify assistant with id:', assistantId);
    const result = await axios.post(`/api/v1/assistants/${assistantId}`, assistant);
    return result.data;
  };

  const createAssistant = async (assistant: Assistant) => {
    console.log('Create assistant.');
    const result = await axios.post(`/api/v1/assistants`, assistant);
    return result.data;
  };

  const retrieveMessages = async (threadId: string) => {
    const result = await axios.get(
        `/api/v1/threads/${threadId}/messages`,
        {
          params: {
            order: Order.ASCENDING,
          },
        },
    );

    const parsedMessageList = result.data;

    const list = parsedMessageList.messageList?.map((message: any) => {
      return new ThreadMessage(message.id, message.role, message.parsedContent,
          message.assistantId);
    });
    chatStore.threadMessages = list || [];

    chatStore.parsedMessages = parsedMessageList.parsedMessages || {};

    chatStore.threadMessages.forEach((message) => {
      const parsedContent = chatStore.parsedMessages[message.id];

      if (parsedContent && message.content) {
        const replacedContent = replaceCodeContent(parsedContent.parsed_content);

        if (replacedContent) {
          message.content = replacedContent;
        }
      }
    });

    console.log('Thread messages after transform!', chatStore.threadMessages);
  };

  const createThreadIfNeeded = async (): Promise<string> => {
    if (!chatStore.threadId) {
      const thread = await createThread();
      chatStore.threadId = thread.id;
    }
    return chatStore.threadId;
  };

  const newThread = () => {
    chatStore.newThread();
  };

  const runStreamAndHandleResults = async (message: string, chunkUpdateCallback: () => void) => {
    console.log('Running stream and handling results');
    await streamRun(chatStore.selectedAssistant.id, chatStore.threadId, message,
        chunkUpdateCallback);

    // Get text content of last user message
    const lastUserMessageContent = getThreadMessageTextContent(chatStore.getLastUserMessage());
    const lastAssistantMessageContent = getThreadMessageTextContent(
        chatStore.getLastAssistantMessage());

    await generateThreadTitle(chatStore.threadId, lastUserMessageContent,
        lastAssistantMessageContent);

    chatStore.updateStatus('', '');
  };

  const retrieveLastAssistentMessage = async (threadId: string): Promise<ThreadMessage | undefined> => {
    const result = await axios.get(
        `/api/v1/threads/${threadId}/messages`,
        {
          params: {
            limit: 1,
            order: Order.DESCENDING,
          },
        },
    );
    const response = result.data;
    console.log('Response: ', response);

    if (response?.messageList.data && response.messageList.data.length > 0) {
      return response.messageList.data[0];
    }
  };

  const handleResult = async () => {
    const threadMessage = await retrieveLastAssistentMessage(chatStore.threadId);
    if (threadMessage) {
      const parsedThreadMessage = await postprocessMessage(chatStore.threadId,
          threadMessage.id);

      if (threadMessage.content && threadMessage.content.length > 0 &&
          threadMessage.content) {
        const content = parsedThreadMessage.parsedContent;

        if (content) {
          threadMessage.content = replaceCodeContent(content);

          // Get text content of last user message
          const lastUserMessage = chatStore.threadMessages[chatStore.threadMessages.length - 2];
          const lastUserMessageContent = getThreadMessageTextContent(lastUserMessage);

          await generateThreadTitle(chatStore.threadId, lastUserMessageContent, content);
        }
      }

      chatStore.threadMessages[chatStore.threadMessages.length - 1] = threadMessage;

      await retrieveThreads();
      console.log('Threads', chatStore.threads);

      chatStore.updateStatus('', '');
    }
  };

  const generateThreadTitle = async (
      threadId: string, userMessage: string, assistantMessage: string) => {
    console.log('Generating title for thread:', threadId);

    const thread = await retrieveThread(threadId);

    if (thread.title && thread.title !== '<no title>' && thread.title !== '') {
      console.log('Thread already has a title:', thread.title);
      return thread.title;
    }

    const result = await axios.post(`/api/v1/threads/${threadId}/title/generate`,
        {
          userMessage,
          assistantMessage,
        });

    console.log('Generated title response', result);
    await retrieveThreads();
  };

  const updateThreadTitle = async (threadId: string, title: string) => {
    console.log('Updating title for thread:', threadId);

    await axios.post(`/api/v1/threads/${threadId}/title`, {title});
    await retrieveThreads();
  };

  const deleteThread = async (threadId: string) => {
    console.log('Delete thread:', threadId);
    await axios.delete(`/api/v1/threads/${threadId}`);
    chatStore.newThread();
    await retrieveThreads();
  };

  const getAssistantImageUrl = (imageUrl: string) => {
    return `/api/v1/assistants/images/${imageUrl}`;
  };

  const encodePrompt = (prompt: string): string => {
    return prompt.replace(/&/g, '&amp;').
        replace(/</g, '&lt;').
        replace(/>/g, '&gt;').
        replace(/"/g, '&quot;').
        replace(/\n/g, '<br/>');
  };

  const cancelCurrentRun = async () => {
    if (chatStore.runId.length > 0) {
      await cancelRun(chatStore.threadId, chatStore.runId);
    }
  };

  const regenerateCurrentRun = async (chunkUpdateCallback: () => void) => {
    await regenerateRun(chatStore.threadId, chatStore.runId);

    chatStore.updateStatus('Thinking...', 'running');
    // await runStreamAndHandleResults(chunkUpdateCallback);
  };

  const submitUserMessage = async (content: string, chunkUpdateCallback: () => void) => {
    console.log('');

    await createThreadIfNeeded();

    // const result = await axios.post(
    //     //     `/api/v1/threads/${chatStore.threadId}/messages`,
    //     //     {content, role: 'user'},
    //     // );
    //     // const submitedMessage = result.data;

    chatStore.threadMessages.push(
        new ThreadMessage('', 'user', content, chatStore.selectedAssistant.id));
    chatStore.threadMessages.push(
        new ThreadMessage('', 'assistant', '', chatStore.selectedAssistant.id));

    chatStore.updateStatus('Thinking...', 'running');

    await runStreamAndHandleResults(content, chunkUpdateCallback);
  };

  const runConversation = async () => {
    await axios.post(
        `/api/v1/threads/${chatStore.threadId}/runs`,
        {assistantId: chatStore.selectedAssistant.id},
    );
  };

  return {
    streamRun,
    selectAssistant,
    retrieveAssistant,
    retrieveAssistants,
    modifyAssistant,
    createAssistant,
    deleteAssistant,
    retrieveThreads,
    getAssistantById,
    retrieveMessages,
    createThreadIfNeeded,
    newThread,
    runStreamAndHandleResults,
    generateThreadTitle,
    updateThreadTitle,
    deleteThread,
    getAssistantImageUrl,
    encodePrompt,
    cancelCurrentRun,
    regenerateCurrentRun,
    submitUserMessage,
    runConversation,
    retrieveGPTModels,
    uploadAssistantImage,
    generateAssistantImage,
  };
}