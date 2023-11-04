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

import ChatMessage, {FunctionCall} from '@/store/to/chat-message';
import Role from '@/store/to/role';
import {useChatStore} from '@/store/chat-store';
import axios from 'axios';
import debounce from 'lodash/debounce';
import HighlightingService from '@/service/highlighting.service';

const highlightingService = new HighlightingService();

const DELAY_TIME = 20;
const DEBOUNCE_TIME = 200;
const DEBOUNCE_MAXWAIT = 1000;

class ChatStreamService {
  _buffer = '';

  async streamSubmit(sessionId: string, content: string, chunkUpdateCallback: () => void) {
    const debouncedUpdateCallback = debounce(chunkUpdateCallback, DEBOUNCE_TIME, {maxWait: DEBOUNCE_MAXWAIT});

    const store = useChatStore();
    const isFunctionCall = false;

    const newMessage = new ChatMessage(Role.ASSISTANT, '');
    store.messages.push(newMessage);
    store.updateStatus('Thinking...', 'running');
    chunkUpdateCallback();

    const response = await this.fetchSSE(content, sessionId);
    const reader = response.body?.getReader();

    if (!reader) return;

    const decoder = new TextDecoder('utf-8');
    let partial = '';

    let isReading = true;

    while (isReading) {
      const {done, value} = await reader.read();

      if (done) {
        await this.postStreamProcessing(store, sessionId, isFunctionCall);
        store.removeStatus();
        isReading = false;
      } else if (value) {
        const chunk = decoder.decode(value, {stream: true});
        partial += chunk;
        const parts = partial.split('\n');
        partial = parts.pop() || '';
        this._buffer = '';
        for (const part of parts) {
          if (!part.startsWith('data:')) continue;
          const data = part.substring(5);
          this.processData(data, store, debouncedUpdateCallback);
          await this.sleep(DELAY_TIME);
        }
      }
    }
  }

  async postStreamProcessing(store: any, sessionId: string, isFunctionCall: boolean) {
    if (!isFunctionCall) {
      const processedMessage = await this.postProcessLastMessage(sessionId);
      processedMessage.content = highlightingService.replaceCodeContent(processedMessage.content);

      store.messages.pop();
      store.messages.push(processedMessage);
    }
  }

  private escapeHtml(html: string): string {
    const tagsToReplace: { [key: string]: string } = {
      '<': '&lt;',
      '>': '&gt;',
    };

    return html.replace(/[<>]/g, (tag: string) => tagsToReplace[tag] || tag);
  }

  private async sleep(delay: number) {
    return new Promise((resolve) => setTimeout(resolve, delay));
  }

  private processData(data: string, store: any, debouncedUpdateCallback: () => void) {
    if (!this.hasJSONData(data)) return;

    const chatChoice = JSON.parse(data);
    const lastMessage = store.messages[store.messages.length - 1];

    if (!chatChoice?.delta) return;

    if (chatChoice.delta.content) {
      let newContent = this.escapeHtml(chatChoice.delta.content);
      newContent = newContent.replaceAll(/\n/g, '<br/>');
      lastMessage.content += newContent;
      debouncedUpdateCallback();
    }

    if (chatChoice.delta.function_call?.arguments) {
      if (!lastMessage?.function_call) {
        lastMessage.function_call = new FunctionCall();
      }
      lastMessage.function_call.name = chatChoice.delta.function_call.name;
      lastMessage.function_call.arguments += chatChoice.delta.function_call.arguments;
    }
  }

  private hasJSONData(data: string): boolean {
    return data.indexOf('{') != -1;
  }

  private async postProcessLastMessage(sessionId: string): Promise<ChatMessage> {
    try {
      const result = await axios.get(`/api/v1/session/${sessionId}/postprocess/last`);
      return result.data;
    } catch (error) {
      throw new Error('Error reading session entry:  ' + error);
    }
  }

  private async fetchSSE(content: string, sessionId: string): Promise<Response> {
    return await fetch('/api/v1/chat/stream/submit', {
      method: 'POST',
      cache: 'no-cache',
      keepalive: true,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'text/event-stream',
      },
      body: JSON.stringify({
        content,
        sessionId,
      }),
    });
  }

}

export default ChatStreamService;

