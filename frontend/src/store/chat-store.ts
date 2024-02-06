/*
 * Copyright (c) 2023-2024 Jean Schmitz.
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

import {defineStore} from 'pinia';
import HighlightingService from '@/service/highlighting.service';
import AssistantService from '@/service/assistant.service';
import Thread, {ThreadMessage} from '@/store/to/thread';
import Assistant from '@/store/to/assistant';
import AssistantProperties, {TTSType} from '@/service/assistant.properties';

const highlightingService = new HighlightingService();
const assistantService = new AssistantService();

export const useChatStore = defineStore('chat', {
  state: () => {
    return {
      isAutoSpeak: false,
      currentStatusMessage: '',
      currentStatusMessageType: '',
      threadMessages: [] as Array<ThreadMessage>,
      parsedMessages: {} as any,
      threadId: '',
      runId: '',
      threads: [] as Array<Thread>,
      threadEditMode: false,
      threadDeleteMode: false,
      selectedAssistant: {} as Assistant,
      assistantList: [] as Array<Assistant>,
    };
  },
  getters: {
    isTTSEnabled(): boolean {
      return this.selectedAssistant?.properties?.[AssistantProperties.TTS_TYPE] !==
          TTSType.DISABLED;
    },
    autoSpeak(): boolean {
      return this.isAutoSpeak;
    },
    isEmptyThread(): boolean {
      return this.threadMessages.length == 0;
    },
    maxMessageIndex(): number {
      return this.threadMessages.length - 1;
    },
  },
  actions: {
    async syncAssistants() {
      await assistantService.syncAssistants();
      await this.retrieveAssistants();
    },
    async retrieveAssistants() {
      this.assistantList = await assistantService.retrieveAssistants();
    },
    async selectAssistant(assistantId: string) {
      this.selectedAssistant = await assistantService.retrieveAssistant(assistantId);
    },
    async retrieveThreads() {
      // Make this.threads a list of Thread objects
      this.threads = await assistantService.retrieveThreads();
    },
    async retrieveMessages(threadId: string) {
      const parsedMessageList = await assistantService.retrieveMessages(threadId);

      const list = parsedMessageList.message_list?.data?.map((message: any) => {
        return new ThreadMessage(message.id, message.role, message.content[0].text.value,
            message.assistant_id);
      });
      this.threadMessages = list || [];

      this.parsedMessages = parsedMessageList.parsed_messages || {};

      this.threadMessages.forEach((message) => {
        const parsedContent = this.parsedMessages[message.id];

        if (parsedContent && message.content?.[0]?.text) {
          const replacedContent = highlightingService.replaceCodeContent(parsedContent);

          if (replacedContent) {
            message.content[0].text.value = replacedContent;
          }
        }
      });

      console.log('Thread messages after transform!', this.threadMessages);
    },
    async createThreadIfNeeded(): Promise<string> {
      if (!this.threadId) {
        const thread = await assistantService.createThread();
        this.threadId = thread.id;
      }
      return this.threadId;
    },
    async newThread() {
      this.threadId = '';
      this.threadMessages = [];
      this.parsedMessages = {};
      this.threadEditMode = false;
      this.threadDeleteMode = false;
    },
    async runConversationAndHandleResults() {
      let pollingInterval: undefined | number = undefined;
      try {
        const run = await assistantService.runConversation(this.threadId,
            this.selectedAssistant.id);
        this.runId = run.id;

        pollingInterval = setInterval(async () => {
          console.log('Polling for run status');
          const runT = await assistantService.retrieveRun(this.threadId, run.id);
          if (runT.status === 'completed') {
            console.log('Run completed');
            clearInterval(pollingInterval);
            this.runId = '';
            await this.handleResult();
          } else if (runT.status === 'cancelled') {
            console.log('Run cancelled');
            clearInterval(pollingInterval);
            this.runId = '';
            this.updateStatus('Cancelled', 'error');
          }
        }, 2000);
      } catch (e) {
        console.error('Error while handling result', e);
        if (pollingInterval) clearInterval(pollingInterval);
      }
    },
    async cancelCurrentRun() {
      if (this.runId.length > 0) {
        await assistantService.cancelRun(this.threadId, this.runId);
      }
    },
    // Usage of the refactored functions
    async submitUserMessage(message: string) {
      await this.createThreadIfNeeded();

      const submitedMessage = await assistantService.submitUserMessage(this.threadId, message);
      this.threadMessages.push(submitedMessage);
      this.threadMessages.push(new ThreadMessage('', 'assistant', '', this.selectedAssistant.id));

      this.updateStatus('Thinking...', 'running');

      await this.runConversationAndHandleResults();
    },
    getThreadMessageTextContent(threadMessage: ThreadMessage) {
      if (threadMessage.content && threadMessage.content.length > 0 &&
          threadMessage.content[0].text) {
        return threadMessage.content[0].text.value || '';
      }
      return '';
    },
    async handleResult() {
      const threadMessage = await assistantService.retrieveLastAssistentMessage(this.threadId);
      if (threadMessage) {
        const parsedThreadMessage = await assistantService.postprocessMessage(this.threadId,
            threadMessage.id);

        if (threadMessage.content && threadMessage.content.length > 0 &&
            threadMessage.content[0].text) {
          const content = parsedThreadMessage.parsed_content;

          if (content) {
            const highlightedContent = highlightingService.replaceCodeContent(content);
            threadMessage.content[0].text.value = highlightedContent;

            // Get text content of last user message
            const lastUserMessage = this.threadMessages[this.threadMessages.length - 2];
            const lastUserMessageContent = this.getThreadMessageTextContent(lastUserMessage);

            await this.generateThreadTitle(this.threadId, lastUserMessageContent, content);
          }
        }

        this.threadMessages[this.threadMessages.length - 1] = threadMessage;

        this.threads = await assistantService.retrieveThreads();
        console.log('Threads', this.threads);

        this.updateStatus('', '');
      }
    },
    async runConversation() {
      await assistantService.runConversation(this.threadId, this.selectedAssistant.id);
    },
    async generateThreadTitle(threadId: string, userMessage: string, assistantMessage: string) {
      // Find selected thread
      const response = await assistantService.generateThreadTitle(threadId, userMessage,
          assistantMessage);
      console.log('Generated title response', response);
      await this.retrieveThreads();
    },
    async updateThreadTitle(threadId: string, title: string) {
      await assistantService.updateThreadTitle(threadId, title);
      await this.retrieveThreads();
    },
    async deleteThread(threadId: string) {
      await assistantService.deleteThread(threadId);
      await this.retrieveThreads();
    },
    getAssistantImageUrl(imageUrl: string) {
      return assistantService.getAssistantImageUrl(imageUrl);
    },
    // ************** Old code *****************

    encodePrompt(prompt: string): string {
      return prompt.replace(/&/g, '&amp;').
          replace(/</g, '&lt;').
          replace(/>/g, '&gt;').
          replace(/"/g, '&quot;').
          replace(/\n/g, '<br/>');
    },
    toggleAutoSpeak() {
      this.isAutoSpeak = !this.isAutoSpeak;
    },
    updateStatus(message: string, type = '') {
      this.currentStatusMessage = message;
      this.currentStatusMessageType = type;
    },
    removeStatus() {
      this.currentStatusMessage = '';
      this.currentStatusMessageType = '';
    },
  },

});
