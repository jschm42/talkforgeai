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

import {defineStore} from 'pinia';
import Session from '@/store/to/session';
import ChatMessage from '@/store/to/chat-message';
import Persona from '@/store/to/persona';
import ChatService from '@/service/chat.service';
import PersonaService from '@/service/persona.service';
import Role from '@/store/to/role';
import ChatStreamService from '@/service/chat-stream.service';
import HighlightingService from '@/service/highlighting.service';
import PersonaProperties, {TTSType} from '@/service/persona.properties';
import AssistantService from '@/service/assistant.service';
import Thread, {ThreadMessage} from '@/store/to/thread';
import Assistant from '@/store/to/assistant';

const chatService = new ChatService();
const chatStreamService = new ChatStreamService();
const personaService = new PersonaService();
const highlightingService = new HighlightingService();
const assistantService = new AssistantService();

export const useChatStore = defineStore('chat', {
  state: () => {
    return {
      sessionId: '',
      messages: [] as Array<ChatMessage>,
      selectedPersona: {} as Persona,
      personaList: [] as Array<Persona>,
      chat: {
        locked: false,
        configHeaderEnabled: true,
        autoSpeak: false,
      },

      sessions: [] as Array<Session>,
      selectedSessionId: '',

      // Assistant API
      currentStatusMessage: '',
      currentStatusMessageType: '',
      threadMessages: [] as Array<ThreadMessage>,
      parsedMessages: {} as any,
      threadId: '',
      threads: [] as Array<Thread>,
      selectedAssistant: {} as Assistant,
      assistantList: [] as Array<Assistant>,
    };
  },
  getters: {
    isTTSEnabled(): boolean {
      if (this.selectedPersona && this.selectedPersona.properties) {
        return this.selectedPersona.properties[PersonaProperties.TTS_TYPE] !== TTSType.DISABLED;
      }
      return false;
    },
    autoSpeak(): boolean {
      return this.chat.autoSpeak;
    },
    isEmptyThread(): boolean {
      return this.threadMessages.length == 0;
    },
    maxMessageIndex(): number {
      return this.threadMessages.length - 1;
    },
  },
  actions: {
    async selectAssistant(assistantId: string) {
      this.selectedAssistant = await assistantService.retrieveAssistant(assistantId);
    },
    async retrieveThreads() {
      this.threads = await assistantService.retrieveThreads();
    },
    async retrieveMessages(threadId: string) {
      const parsedMessageList = await assistantService.retrieveMessages(threadId);

      this.threadMessages = parsedMessageList.message_list?.data || [];
      this.parsedMessages = parsedMessageList.parsed_messages || {};

      this.threadMessages.forEach((message) => {
        // Update content with parsed content
        if (message.content && message.content.length > 0 && message.content[0].text) {
          if (this.parsedMessages[message.id]) {
            let content = this.parsedMessages[message.id];

            if (content) {
              content = highlightingService.replaceCodeContent(content);
            }

            message.content[0].text.value = content;
          }
        }
      });
    },
    async submitUserMessage(message: string) {
      if (!this.threadId) {
        const thread = await assistantService.createThread();
        this.threadId = thread.id;
      }

      const submitedMessage = await assistantService.submitUserMessage(this.threadId, message);
      this.threadMessages.push(submitedMessage);
      this.threadMessages.push(new ThreadMessage('', 'assistant', ''));

      this.updateStatus('Thinking...', 'running');

      const run = await assistantService.runConversation(this.threadId, this.selectedAssistant.id);

      const pollingInterval = setInterval(async () => {
        console.log('Polling for run status');
        const runT = await assistantService.retrieveRun(this.threadId, run.id);
        if (runT.status === 'completed') {
          console.log('Run completed');
          try {
            clearInterval(pollingInterval);
            await this.handleResult();
          } catch (e) {
            console.error('Error while handling result', e);
          } finally {
            clearInterval(pollingInterval);
          }
        }
      }, 2000);

    },
    getThreadMessageTextContent(threadMessage: ThreadMessage) {
      if (threadMessage.content && threadMessage.content.length > 0 && threadMessage.content[0].text) {
        return threadMessage.content[0].text.value || '';
      }
      return '';
    },
    async handleResult() {
      console.log('Handling result', this.threadId);
      const threadMessage = await assistantService.retrieveLastAssistentMessage(this.threadId);
      console.log('Received result', threadMessage);
      if (threadMessage) {
        const parsedThreadMessage = await assistantService.postprocessMessage(this.threadId, threadMessage.id);

        if (threadMessage.content && threadMessage.content.length > 0 && threadMessage.content[0].text) {
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
      const thread = this.threads.find(t => t.id === threadId);
      if (thread && (thread.title === '<no title>' || thread.title === '' || thread.title === undefined)) {
        const response = await assistantService.generateThreadTitle(threadId, userMessage, assistantMessage);
        console.log('Generated title response', response);
        await this.retrieveThreads();
      }
    },
    // ************** Old code *****************
    async newSession() {
      const thread = await assistantService.createThread();

      this.$patch({
        threadMessages: [],
        parsedMessages: {},
        chat: {
          configHeaderEnabled: true,
        },
        currentStatusMessage: '',
        threadId: thread.id,
      });
    },
    resetChat() {
      this.$patch({
        sessionId: '',
        messages: [],
        currentStatusMessage: '',
        selectedSessionId: '',
        sessions: [],
        chat: {
          autoSpeak: false,
          configHeaderEnabled: true,
        },
      });
    },
    async selectPersona(persona: Persona): Promise<void> {
      this.resetChat();
      this.selectedPersona = persona;
      this.chat.autoSpeak = persona.properties[PersonaProperties.FEATURE_AUTOSPEAKDEFAULT] === 'true';
      await this.loadIndex(persona.personaId);
    },
    async selectPersonaById(personaId: string): Promise<void> {
      if (this.personaList.length === 0) {
        this.personaList = await personaService.readAllPersona();
      }
      const persona = this.personaList.find(p => p.personaId === personaId);
      console.log('selectedPersona', persona);
      if (persona) {
        await this.selectPersona(persona);
      }
    },
    async loadIndex(personaId: string): Promise<void> {
      this.sessions = await chatService.readSessionEntries(personaId);
    },
    encodePrompt(prompt: string): string {
      return prompt.replace(/&/g, '&amp;').
        replace(/</g, '&lt;').
        replace(/>/g, '&gt;').
        replace(/"/g, '&quot;').
        replace(/\n/g, '<br/>');
    },
    async streamPrompt(prompt: string, chunkUpdateCallback: () => void) {
      this.chat.configHeaderEnabled = false;

      if (!this.sessionId || this.sessionId === '') {
        this.sessionId = await chatService.createNewSession(this.selectedPersona.personaId);
      }

      this.selectedSessionId = this.sessionId;

      // Encode html in prompt
      const encodedPrompt = this.encodePrompt(prompt);

      this.messages.push(new ChatMessage(Role.USER, encodedPrompt));
      chunkUpdateCallback();

      console.log('Submitting prompt', this.sessionId, prompt);

      try {
        await chatStreamService.streamSubmit(this.sessionId, prompt, chunkUpdateCallback);
      } catch (e) {
        console.error('Error while streaming prompt', e);
        throw e;
      }

      if (this.isSessionTitleGenerationEnabled()) {
        /**
         try {
         await this.generateSessionTitle(this.sessionId);
         } catch (e) {
         console.error('Error while generating session title', e);
         throw e;
         }
         **/
      } else {
        console.log('Session title generation is disabled. Skipping.');
      }

      try {
        await this.loadIndex(this.selectedPersona.personaId);
      } catch (e) {
        console.error('Error while loading index', e);
        throw e;
      }
    },
    async loadChatSession(sessionId: string) {
      const chatSession = await chatService.readSessionEntry(sessionId);

      highlightingService.highlightCodeInChatMessage(chatSession.chatMessages);

      this.$patch({
        sessionId: chatSession.id,
        messages: chatSession.chatMessages,
        selectedPersona: chatSession.persona,
        chat: {configHeaderEnabled: false},
      });

    },
    async updateSessionTitle(sessionId: string, newTitle: string) {
      await chatService.updateSessionTitle(sessionId, newTitle);
    },
    isSessionTitleGenerationEnabled() {
      return this.selectedPersona.properties[PersonaProperties.FEATURE_TITLEGENERATION] === 'true';
    },
    hasEmptySessionTitle(sessionId: string) {
      const currentSession = this.sessions.find(s => s.id === sessionId);
      console.log('currentSession', currentSession);
      if (currentSession) {
        return currentSession.title === '' || currentSession.title === undefined || currentSession.title === '<empty>';
      }
      return true;
    },

    async deleteChatSession(sessionId: string) {
      await chatService.deleteSession(this.sessionId);
      this.selectedSessionId = '';

      const foundSession = this.sessions.find((e) => e.id === sessionId);
      if (foundSession) {
        this.sessions.splice(this.sessions.indexOf(foundSession), 1);
      }

      if (this.sessions.length > 0) {
        this.selectedSessionId = this.sessions[0].id;
        await this.loadChatSession(this.selectedSessionId);
      } else {
        this.resetChat();
      }

    },
    async readPersona() {
      this.personaList = await personaService.readAllPersona();
      this.selectedPersona = this.personaList[0];
    },
    toggleAutoSpeak() {
      this.chat.autoSpeak = !this.chat.autoSpeak;
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
