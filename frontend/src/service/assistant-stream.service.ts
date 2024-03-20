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

import {useChatStore} from '@/store/chat-store';
import axios from 'axios';
import debounce from 'lodash/debounce';
import HighlightingService from '@/service/highlighting.service';
import {ParsedThreadMessage} from '@/store/to/thread';

const highlightingService = new HighlightingService();

const DELAY_TIME = 20;
const DEBOUNCE_TIME = 200;
const DEBOUNCE_MAXWAIT = 1000;

class AssistantStreamService {
  _buffer = '';

  async streamRun(
      assistantId: string, threadId: string, chunkUpdateCallback: () => void) {
    const debouncedUpdateCallback = debounce(chunkUpdateCallback, DEBOUNCE_TIME,
        {maxWait: DEBOUNCE_MAXWAIT});

    const store = useChatStore();
    const isFunctionCall = false;

    chunkUpdateCallback();

    const response = await this.fetchSSE(assistantId, threadId);
    const reader = response.body?.getReader();

    if (!reader) return;

    const decoder = new TextDecoder('utf-8');
    let partial = '';

    let isReading = true;

    while (isReading) {
      const chunk = await reader.read();
      //const {done, value} = await reader.read();

      const chunkValue = decoder.decode(chunk.value, {stream: true});
      console.log('--> chunk', chunkValue);

      if (chunk.done) {
        const lastMessage = store.threadMessages[store.threadMessages.length - 1];
        console.log('--> lastMessage', lastMessage);
        await this.postStreamProcessing(store, threadId, chunkValue);
        store.removeStatus();
        isReading = false;
      } else {
        partial += chunkValue;
        const parts = partial.split('\n');
        partial = parts.pop() || '';
        this._buffer = '';
        for (const part of parts) {
          if (part.startsWith('data:')) {
            const data = part.substring(5);
            this.processData(data, store, debouncedUpdateCallback);
            await this.sleep(DELAY_TIME);
          } else if (part.startsWith('event:')) {
            const event = part.substring(6);
            console.log('--> event', event);
          }
        }
      }
    }
  }

  async postStreamProcessing(store: any, threadId: string, messageId: string) {
    const processedMessage = await this.postprocessMessage(threadId, messageId);

    console.log('--> processedMessage', processedMessage);

    // if (processedMessage) {
    //   const codeContent = highlightingService.replaceCodeContent(processedMessage.parsed_content);
    // }

    store.threadMessages.pop();
    store.threadMessages.push(processedMessage);
  }

  async postprocessMessage(threadId: string, messageId: string): Promise<ParsedThreadMessage> {
    const result = await axios.post(
        `/api/v1/threads/${threadId}/messages/${messageId}/postprocess`,
        {},
    );
    return result.data;
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
    console.log('--> data', data);
    if (!this.hasJSONData(data)) return;

    const content = JSON.parse(data)['delta']['content'];
    console.log('--> content', content);
    const lastMessage = store.threadMessages[store.threadMessages.length - 1];

    if (content.length > 0 && content[0].type === 'text') {
      const textContent = content[0].text.value;
      console.log('--> content.text', textContent);
      let newContent = this.escapeHtml(textContent);
      newContent = newContent.replaceAll(/\n/g, '<br/>');
      lastMessage.content[0].text.value += newContent;
      //lastMessage.content += newContent;
      debouncedUpdateCallback();
    }

    // if (chatChoice.delta.function_call?.arguments) {
    //   if (!lastMessage?.function_call) {
    //     lastMessage.function_call = new FunctionCall();
    //   }
    //   lastMessage.function_call.name = chatChoice.delta.function_call.name;
    //   lastMessage.function_call.arguments += chatChoice.delta.function_call.arguments;
    // }
  }

  private hasJSONData(data: string): boolean {
    return data.indexOf('{') != -1;
  }

  private async fetchSSE(assistantId: string, threadId: string): Promise<Response> {
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
      }),
    });
  }

}

export default AssistantStreamService;

