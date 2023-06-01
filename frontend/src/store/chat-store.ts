import {defineStore} from 'pinia';
import Session from '@/store/to/session';
import ChatMessage from '@/store/to/chat-message';
import Persona from '@/store/to/persona';
import ChatService from '@/service/chat.service';
import PersonaService from '@/service/persona.service';

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

      sessions: [] as Array<Session>,
    };
  },
  getters: {
    autoSpeak(): boolean {
      return this.chat.autoSpeak;
    },
    isEmptySession(): boolean {
      return this.messages.length == 0;
    },
  },
  actions: {
    async newSession() {
      if (this.selectedPersona) {
        this.sessionId = await chatService.createNewSession(this.selectedPersona.personaId);
      }
      this.messages = [];
    },
    disableConfigHeader() {
      console.log('disableConfigHeader');
      this.chat.configHeaderEnabled = false;
    },
    async loadIndex() {
      this.sessions = await chatService.readSessionEntries();
    },
    async submitPrompt(prompt: string) {
      const result = await chatService.submit(this.sessionId, prompt);
      this.messages = [...this.messages, ...result.processedMessages];
    },
    loadChatSession(sessionId: string) {
      //const chatSession = window.chatAPI.loadChatSession(sessionId);
      //console.log('Loaded chat session with ID: ' + sessionId, chatSession);

      this.$patch({
        //session: chatSession,
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
  },

});
