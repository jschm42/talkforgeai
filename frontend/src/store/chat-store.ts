import {defineStore} from 'pinia';
import Session from '@/store/to/session';
import ChatMessage from '@/store/to/chat-message';
import Persona from '@/store/to/persona';
import ChatService from '@/service/chat.service';
import PersonaService from '@/service/persona.service';
import Role from '@/store/to/role';

const chatService = new ChatService();
const personaService = new PersonaService();

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
      selectedSessionId: null,
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
    async newSession() {
      this.sessionId = '';
      this.messages = [];
      this.chat.configHeaderEnabled = true;
    },
    disableConfigHeader() {
      console.log('disableConfigHeader');
      this.chat.configHeaderEnabled = false;
    },
    async loadIndex() {
      this.sessions = await chatService.readSessionEntries();
    },
    async getLastResult() {
      return await chatService.getLastResult(this.sessionId);
    },
    async sendFunctionConfirm(sessionId: string) {
      const result = await chatService.submitFunctionConfirm(sessionId);
    },
    async submitPrompt(prompt: string) {
      this.chat.configHeaderEnabled = false;

      if (!this.sessionId || this.sessionId === '') {
        this.sessionId = await chatService.createNewSession(this.selectedPersona.personaId);
      }

      this.messages.push(new ChatMessage(Role.USER, prompt));

      console.log('Submitting prompt', this.sessionId, prompt);
      const result = await chatService.submit(this.sessionId, prompt);
      console.log('Prompt submitted');
      //this.messages = [...this.messages, ...result.processedMessages];
    },
    async loadChatSession(sessionId: string) {
      const chatSession = await chatService.readSessionEntry(sessionId);

      this.$patch({
        sessionId: chatSession.id,
        messages: chatSession.chatMessages,
        selectedPersona: chatSession.persona,
        chat: {configHeaderEnabled: false},
      });
    },
    async readPersona() {
      this.personaList = await personaService.readPersona();
      this.selectedPersona = this.personaList[0];
    },
    getElevenLabsProperties() {
      //return this.session.persona.elevenLabsProperties;
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
