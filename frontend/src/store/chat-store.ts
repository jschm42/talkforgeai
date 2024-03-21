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
import Thread, {ThreadMessage} from '@/store/to/thread';
import Assistant from '@/store/to/assistant';
import AssistantProperties, {TTSType} from '@/const/assistant.properties';

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
    async newThread() {
      this.threadId = '';
      this.threadMessages = [];
      this.parsedMessages = {};
      this.threadEditMode = false;
      this.threadDeleteMode = false;
    },
    getLastUserMessage() {
      // Find the last user message in this.threadMessages array by iterating in reverse
      for (let i = this.threadMessages.length - 1; i >= 0; i--) {
        if (this.threadMessages[i].role === 'user') {
          return this.threadMessages[i];
        }
      }
    },
    getLastAssistantMessage() {
      // Find the last assistant message in this.threadMessages array by iterating in reverse
      for (let i = this.threadMessages.length - 1; i >= 0; i--) {
        if (this.threadMessages[i].role === 'assistant') {
          return this.threadMessages[i];
        }
      }
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
