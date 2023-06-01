import {defineStore} from 'pinia';
import Session from '@/store/to/session';
import ChatMessage from '@/store/to/chat-message';
import Persona from '@/store/to/persona';
import ChatService from '@/service/chat.service';
import PersonaService from '@/service/persona.service';
import ChatSession from '@/store/to/chat-session';

const chatService = new ChatService();
const personaService = new PersonaService();

export const useChatStore = defineStore('chat', {
  state: () => {
    return {
      session: new ChatSession(),
      messages: [] as Array<ChatMessage>,
      chat: {
        configHeaderEnabled: true,
        autoSpeak: false,
      },
      persona: [] as Array<Persona>,
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
      this.session.sessionId = await chatService.createNewSession();
      this.session.messages = [];
    },
    disableConfigHeader() {
      console.log('disableConfigHeader');
      this.chat.configHeaderEnabled = false;
    },
    async loadIndex() {
      this.sessions = await chatService.readSessionEntries();
    },
    async submitPrompt(prompt: string) {
      const result = await chatService.submit(this.session.sessionId, prompt);
      this.session.messages = result.processedMessages;
    },
    loadChatSession(sessionId: string) {
      //const chatSession = window.chatAPI.loadChatSession(sessionId);
      //console.log('Loaded chat session with ID: ' + sessionId, chatSession);

      this.$patch({
        //session: chatSession,
        chat: {configHeaderEnabled: false},
      });
    },
    changePersona(personaName: string) {
      // TODO
    },
    async readPersona() {
      this.persona = await personaService.readPersona();
    },
    getElevenLabsProperties() {
      //return this.session.persona.elevenLabsProperties;
    },
    toggleAutoSpeak() {
      this.chat.autoSpeak = !this.chat.autoSpeak;
    },
  },

});
