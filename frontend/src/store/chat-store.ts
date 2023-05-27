import {defineStore} from 'pinia';
import Session from '@/store/to/session';
import ChatMessage from '@/store/to/chat-message';
import Persona from '@/store/to/persona';
import ChatService from '@/service/chat.service';

const chatService = new ChatService();

export const useChatStore = defineStore('chat', {
  state: () => {
    return {
      sessionId: '' as string,
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
    newSession() {
      // TODO
    },
    disableConfigHeader() {
      console.log('disableConfigHeader');
      this.chat.configHeaderEnabled = false;
    },
    loadIndex() {
      chatService.readSessionEntries().then((entries: any) => {
        console.log('Index entries loaded', entries.data);
        this.sessions = entries.data;
      });
    },
    saveIndex() {
      //const indexRaw = toRaw(this.index.entries);

      //window.chatIndexAPI.save(indexRaw);
      //console.log('Index saved', indexRaw);
    },
    addIndexEntry(entry: Session) {
      // Insert entry at the start of the index
      //this.index.entries.unshift(entry);
      //this.saveIndex();
    },
    loadChatSession(sessionId: string) {
      //const chatSession = window.chatAPI.loadChatSession(sessionId);
      //console.log('Loaded chat session with ID: ' + sessionId, chatSession);

      this.$patch({
        //session: chatSession,
        chat: {configHeaderEnabled: false},
      });
    },
    async submitStreamPrompt(prompt: string) {
      //return await chatRenderer.submit(prompt, this.session);
    },
    changePersona(personaName: string) {
      // TODO
    },
    async readPersonas() {
      //this.persona = await window.personaAPI.readPersonas();
    },
    getElevenLabsProperties() {
      //return this.session.persona.elevenLabsProperties;
    },
    toggleAutoSpeak() {
      this.chat.autoSpeak = !this.chat.autoSpeak;
    },
  },

});
