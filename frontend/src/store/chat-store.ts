import {defineStore} from 'pinia';
import Session from '@/store/to/session';
import ChatMessage from '@/store/to/chat-message';
import Persona from '@/store/to/persona';
import ChatService from '@/service/chat.service';
import PersonaService from '@/service/persona.service';
import Role from '@/store/to/role';
import ChatStreamService from '@/service/chat-stream.service';
import HighlightingService from '@/service/highlighting.service';

const chatService = new ChatService();
const chatStreamService = new ChatStreamService();
const personaService = new PersonaService();
const highlightingService = new HighlightingService();

export const useChatStore = defineStore('chat', {
  state: () => {
    return {
      sessionId: '',
      messages: [] as Array<ChatMessage>,
      selectedPersona: {} as Persona,
      personaList: [] as Array<Persona>,
      chat: {
        configHeaderEnabled: true,
        autoSpeak: false,
      },
      currentStatusMessage: '',
      sessions: [] as Array<Session>,
      selectedSessionId: '',
    };
  },
  getters: {
    autoSpeak(): boolean {
      return this.chat.autoSpeak;
    },
    isEmptySession(): boolean {
      return this.messages.length == 0;
    },
    maxMessageIndex(): number {
      return this.messages.length - 1;
    },
  },
  actions: {
    newSession() {
      this.$patch({
        sessionId: '',
        messages: [],
        chat: {
          configHeaderEnabled: true,
        },
        currentStatusMessage: '',
        selectedSessionId: '',
      });
    },
    disableConfigHeader() {
      console.log('disableConfigHeader');
      this.chat.configHeaderEnabled = false;
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
    async selectPersona(persona: Persona) {
      this.resetChat();
      this.selectedPersona = persona;
      await this.loadIndex(persona.personaId);
    },
    async loadIndex(personaId: string) {
      this.sessions = await chatService.readSessionEntries(personaId);
    },
    async getLastResult() {
      return await chatService.getLastResult(this.sessionId);
    },
    async streamPrompt(prompt: string, chunkUpdateCallback: () => void) {
      this.chat.configHeaderEnabled = false;

      if (!this.sessionId || this.sessionId === '') {
        this.sessionId = await chatService.createNewSession(this.selectedPersona.personaId);
      }

      this.selectedSessionId = this.sessionId;

      this.messages.push(new ChatMessage(Role.USER, prompt));

      console.log('Submitting prompt', this.sessionId, prompt);

      await chatStreamService.streamSubmit(this.sessionId, prompt, chunkUpdateCallback);

      await this.generateSessionTitle(this.sessionId);
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
    async generateSessionTitle(sessionId: string) {
      const userMessage = this.messages.find(m => m.role === Role.USER);
      const userMessageContent = userMessage ? userMessage.content : '';

      const assistantMessage = this.messages.find(m => m.role === Role.ASSISTANT);
      const assistantMessageContent = assistantMessage ? assistantMessage.content : '';

      const response = await chatService.generateSessionTitle(sessionId, userMessageContent, assistantMessageContent);
      console.log('Generated title response', response);
      if (response) {
        const currentSession = this.sessions.find(s => s.id === this.selectedSessionId);
        if (currentSession) {
          currentSession.title = response.generatedTitle;
        }
      }
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
      this.personaList = await personaService.readPersona();
      this.selectedPersona = this.personaList[0];
    },
    toggleAutoSpeak() {
      this.chat.autoSpeak = !this.chat.autoSpeak;
    },
    updateStatus(sessionId: string, message: string) {
      if (sessionId === this.sessionId) {
        this.currentStatusMessage = message;
      }
    },
  },

});
