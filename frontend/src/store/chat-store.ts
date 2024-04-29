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
import Role from '@/store/to/role';
import ModelSystem from '@/store/to/model-system';

export const useChatStore = defineStore('chat', {
  state: () => {
    return {
      isAutoSpeak: false, // Flag to indicate if auto speak is enabled
      currentStatusMessage: '', // Current status message
      currentStatusMessageType: '', // Type of the current status message
      modelSystems: [] as Array<ModelSystem>,
      threadMessages: [] as Array<ThreadMessage>, // Array of thread messages
      parsedMessages: {} as any, // Object of parsed messages
      threadId: '', // Current thread ID
      runId: '', // Current run ID
      threads: [] as Array<Thread>, // Array of threads
      threadEditMode: false, // Flag to indicate if thread edit mode is enabled
      threadDeleteMode: false, // Flag to indicate if thread delete mode is enabled
      selectedAssistant: {} as Assistant, // Selected assistant
      assistantList: [] as Array<Assistant>, // Array of assistants
    };
  },
  getters: {
    /**
     * Get the auto speak state.
     * @returns {boolean} - The auto speak state.
     */
    autoSpeak(): boolean {
      return this.isAutoSpeak;
    },
    /**
     * Check if the thread is empty.
     * @returns {boolean} - True if the thread is empty, false otherwise.
     */
    isEmptyThread(): boolean {
      return this.threadMessages.length == 0;
    },
    /**
     * Get the maximum message index.
     * @returns {number} - The maximum message index.
     */
    maxMessageIndex(): number {
      return this.threadMessages.length - 1;
    },
  },
  actions: {
    /**
     * Create a new thread.
     */
    async newThread() {
      this.threadId = '';
      this.threadMessages = [];
      this.parsedMessages = {};
      this.threadEditMode = false;
      this.threadDeleteMode = false;
    },
    /**
     * Get the last user message.
     * @returns {ThreadMessage | undefined} - The last user message or undefined if not found.
     */
    getLastUserMessage(): ThreadMessage | undefined {
      for (let i = this.threadMessages.length - 1; i >= 0; i--) {
        if (this.threadMessages[i].role === Role.USER) {
          return this.threadMessages[i];
        }
      }
    },
    /**
     * Get the last assistant message.
     * @returns {ThreadMessage | undefined} - The last assistant message or undefined if not found.
     */
    getLastAssistantMessage(): ThreadMessage | undefined {
      // Find the last assistant message in this.threadMessages array by iterating in reverse
      for (let i = this.threadMessages.length - 1; i >= 0; i--) {
        if (this.threadMessages[i].role === Role.ASSISTANT) {
          return this.threadMessages[i];
        }
      }
    },
    /**
     * Get the last message.
     * @returns {ThreadMessage} - The last message.
     */
    getLastMessage(): ThreadMessage {
      return this.threadMessages[this.threadMessages.length - 1];
    },
    /**
     * Toggle the auto speak state.
     */
    toggleAutoSpeak() {
      this.isAutoSpeak = !this.isAutoSpeak;
    },
    /**
     * Update the status.
     * @param {string} message - The status message.
     * @param {string} type - The status type.
     */
    updateStatus(message: string, type = '') {
      this.currentStatusMessage = message;
      this.currentStatusMessageType = type;
    },
    /**
     * Remove the status.
     */
    removeStatus() {
      this.currentStatusMessage = '';
      this.currentStatusMessageType = '';
    },

  },

});
