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

import axios from 'axios';
import Run from '@/store/to/run';
import Thread, {
  ParsedThreadMessage,
  ThreadMessage,
  TreadMessageListParsed,
} from '@/store/to/thread';
import Assistant from '@/store/to/assistant';

enum Order {
  ASCENDING = 'ASCENDING',
  DESCENDING = 'DESCENDING',
}

class AssistantService {

  async retrieveGPTModels() {
    console.log('Retrieving GPT models');
    const result = await axios.get('/api/v1/assistants/models');
    return result.data;
  }

  async syncAssistants() {
    console.log('Syncing assistants');
    return await axios.post('/api/v1/assistants/sync');
  }

  async retrieveAssistants(): Promise<Array<Assistant>> {
    console.log('Retrieving assistants');
    const result = await axios.get('/api/v1/assistants', {
      params: {
        order: Order.ASCENDING,
      },
    });
    return result.data;
  }

  async retrieveAssistant(assistantId: string): Promise<Assistant> {
    console.log('Retrieving assistant with id:', assistantId);
    const result = await axios.get(`/api/v1/assistants/${assistantId}`);
    return result.data;
  }

  async modifyAssistant(assistantId: string, assistant: Assistant) {
    console.log('Modify assistant with id:', assistantId);
    const result = await axios.post(`/api/v1/assistants/${assistantId}`, assistant);
    return result.data;
  }

  async deleteAssistant(assistantId: string) {
    console.log('Delete assistant with id:', assistantId);
    const result = await axios.delete(`/api/v1/assistants/${assistantId}`);
    return result.data;
  }

  async createAssistant(assistant: Assistant) {
    console.log('Create assistant.');
    const result = await axios.post(`/api/v1/assistants`, assistant);
    return result.data;
  }

  getAssistantImageUrl(imageFile: string) {
    return `/api/v1/assistants/images/${imageFile}`;
  }

  async uploadAssistantImage(file: any) {
    const formData = new FormData();
    formData.append('file', file);

    return await axios.post('/api/v1/assistants/images/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  }

  async generateAssistantImage(prompt: string) {
    return await axios.post(`/api/v1/assistants/images/generate`, {prompt});
  }

  async createThread() {
    const result = await axios.post(`/api/v1/threads`);
    return result.data;
  }

  async submitUserMessage(threadId: string, content: string) {
    const result = await axios.post(
        `/api/v1/threads/${threadId}/messages`,
        {content, role: 'user'},
    );
    return result.data;
  }

  async runConversation(threadId: string, assistantId: string): Promise<Run> {
    const result = await axios.post(
        `/api/v1/threads/${threadId}/runs`,
        {assistantId},
    );
    return result.data;
  }

  async retrieveRun(threadId: string, runId: string): Promise<Run> {
    const result = await axios.get(`/api/v1/threads/${threadId}/runs/${runId}`);
    return result.data;
  }

  async retrieveLastAssistentMessage(threadId: string): Promise<ThreadMessage | undefined> {
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

    if (response.message_list && response.message_list.data && response.message_list.data.length >
        0) {
      return response.message_list.data[0];
    }
  }

  async retrieveMessages(threadId: string): Promise<TreadMessageListParsed> {
    const result = await axios.get(
        `/api/v1/threads/${threadId}/messages`,
        {
          params: {
            order: Order.ASCENDING,
          },
        },
    );

    return result.data;
  }

  async generateThreadTitle(
      threadId: string, userMessageContent: string, assistantMessageContent: string) {
    console.log('Generating title for thread:', threadId);

    const thread = await this.retrieveThread(threadId);

    if (thread.title && thread.title !== '<no title>' && thread.title !== '') {
      console.log('Thread already has a title:', thread.title);
      return thread.title;
    }

    const result = await axios.post(`/api/v1/threads/${threadId}/title/generate`,
        {
          userMessageContent,
          assistantMessageContent,
        });
    return result.data;
  }

  async updateThreadTitle(threadId: string, title: string) {
    console.log('Updating title for thread:', threadId);

    const result = await axios.post(`/api/v1/threads/${threadId}/title`,
        {title});
    return result.data;
  }

  async deleteThread(threadId: string) {
    console.log('Delete thread:', threadId);
    const result = await axios.delete(`/api/v1/threads/${threadId}`);
    return result.data;
  }

  async retrieveThreads(): Promise<Array<Thread>> {
    const result = await axios.get(
        `/api/v1/threads`,
        {
          params: {},
        },
    );

    return result.data;
  }

  async postprocessMessage(threadId: string, messageId: string): Promise<ParsedThreadMessage> {
    const result = await axios.post(
        `/api/v1/threads/${threadId}/messages/${messageId}/postprocess`,
        {},
    );
    return result.data;
  }

  cancelRun(threadId: string, id: string) {
    return axios.post(`/api/v1/threads/${threadId}/runs/${id}/cancel`);
  }

  private async retrieveThread(threadId: string) {
    const result = await axios.get(
        `/api/v1/threads/${threadId}`,
    );
    return result.data;
  }
}

export default AssistantService;
