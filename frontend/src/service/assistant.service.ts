/*
 * Copyright (c) 2023 Jean Schmitz.
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
import Thread, {ParsedThreadMessage, ThreadMessage, TreadMessageListParsed} from '@/store/to/thread';
import Assistant from '@/store/to/assistant';

class AssistantService {

  async retrieveGPTModels() {
    console.log('Retrieving GPT models');
    try {
      const result = await axios.get('/api/v1/assistants/models');
      return result.data;
    } catch (error) {
      throw new Error('Error retrieving GPT models: ' + error);
    }
  }

  async syncAssistants() {
    console.log('Syncing assistants');
    try {
      const result = await axios.post('/api/v1/assistants/sync');
      return result.data;
    } catch (error) {
      throw new Error('Error syncing assistants: ' + error);
    }
  }

  async retrieveAssistants(): Promise<Array<Assistant>> {
    console.log('Retrieving assistants');
    try {
      const result = await axios.get('/api/v1/assistants');
      return result.data;
    } catch (error) {
      throw new Error('Error reading assistants data: ' + error);
    }
  }

  async retrieveAssistant(assistantId: string): Promise<Assistant> {
    console.log('Retrieving assistant with id:', assistantId);
    try {
      const result = await axios.get(`/api/v1/assistants/${assistantId}`);
      return result.data;
    } catch (error) {
      throw new Error('Error reading assistant data: ' + error);
    }
  }

  async modifyAssistant(assistantId: string, assistant: Assistant) {
    console.log('Modify assistant with id:', assistantId);
    try {
      const result = await axios.post(`/api/v1/assistants/${assistantId}`, assistant);
      return result.data;
    } catch (error) {
      throw new Error('Error modifying assistant: ' + error);
    }
  }

  async createAssistant(assistant: Assistant) {
    console.log('Create assistant.');
    try {
      const result = await axios.post(`/api/v1/assistants`, assistant);
      return result.data;
    } catch (error) {
      throw new Error('Error creating assistants: ' + error);
    }
  }

  getAssistantImageUrl(imageFile: string) {
    return `/api/v1/assistants/images/${imageFile}`;
  }

  async generateAssistantImage(prompt: string) {
    try {
      return await axios.post(`/api/v1/assistants/images/generate`, {prompt});
    } catch (error) {
      console.error('Error generating persona image: ', error);
    }
  }

  async createThread() {
    try {
      const result = await axios.post(`/api/v1/threads`);
      return result.data;
    } catch (error) {
      throw new Error('Error creating thread: ' + error);
    }
  }

  async submitUserMessage(threadId: string, content: string) {
    try {
      const result = await axios.post(
        `/api/v1/threads/${threadId}/messages`,
        {content, role: 'user'},
      );
      return result.data;
    } catch (error) {
      throw new Error('Error submitting message: ' + error);
    }
  }

  async runConversation(threadId: string, assistantId: string): Promise<Run> {
    try {
      const result = await axios.post(`/api/v1/threads/${threadId}/runs`, {assistant_id: assistantId});
      return result.data;
    } catch (error) {
      throw new Error('Error submitting message: ' + error);
    }
  }

  async retrieveRun(threadId: string, runId: string): Promise<Run> {
    try {
      const result = await axios.get(`/api/v1/threads/${threadId}/runs/${runId}`);
      return result.data;
    } catch (error) {
      throw new Error('Error retrieving run: ' + error);
    }
  }

  async retrieveLastAssistentMessage(threadId: string): Promise<ThreadMessage | undefined> {
    try {
      const result = await axios.get(
        `/api/v1/threads/${threadId}/messages`,
        {
          params: {
            limit: 1,
            order: 'desc',
          },
        },
      );
      const response = result.data;
      console.log('Response: ', response);

      if (response.message_list && response.message_list.data && response.message_list.data.length > 0) {
        return response.message_list.data[0];
      }
    } catch (error) {
      throw new Error('Error retrieving messages: ' + error);
    }
  }

  async retrieveMessages(threadId: string): Promise<TreadMessageListParsed> {
    try {
      const result = await axios.get(
        `/api/v1/threads/${threadId}/messages`,
        {
          params: {
            order: 'asc',
          },
        },
      );

      return result.data;
    } catch (error) {
      throw new Error('Error retrieving messages: ' + error);
    }
  }

  async generateThreadTitle(threadId: string, userMessageContent: string, assistantMessageContent: string) {
    console.log('Generating title for thread:', threadId);

    const thread = await this.retrieveThread(threadId);

    if (thread.title && thread.title !== '<no title>' && thread.title !== '') {
      console.log('Thread already has a title:', thread.title);
      return thread.title;
    }

    try {
      const result = await axios.post(`/api/v1/threads/${threadId}/title/generate`,
        {
          userMessageContent,
          assistantMessageContent,
        });
      return result.data;
    } catch (error) {
      throw new Error('Error while generating title for thread: ' + error);
    }
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

  private async retrieveThread(threadId: string) {
    const result = await axios.get(
      `/api/v1/threads/${threadId}`,
    );
    return result.data;
  }
}

export default AssistantService;
