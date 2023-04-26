import {defineStore} from 'pinia';
import IndexEntry from '../service/to/index-entry';
import ChatSession from '../service/to/chat-session';
import {toRaw} from 'vue';
import ChatRendererOptimized from '../renderer/char.renderer.optimized';
import {DEFAULT_PERSONA} from '../service/to/persona';

//const chatRenderer = new ChatRenderer();
const chatRenderer = new ChatRendererOptimized();

export const useChatStore = defineStore('chat', {
  state: () => {
    return {
      session: new ChatSession(),
      chat: {
        configHeaderEnabled: true,
      },
      index: {
        entries: [] as Array<IndexEntry>,
      },
    };
  },
  actions: {
    newSession() {
      this.$patch({
        session: new ChatSession(),
        chat: {configHeaderEnabled: true},
      });
    },
    disableConfigHeader() {
      console.log('disableConfigHeader');
      this.chat.configHeaderEnabled = false;
    },
    loadIndex() {
      // @ts-ignore
      const entries = window.chatIndexAPI.load();
      console.log('Index entries loaded', entries);
      this.index.entries = entries;
    },
    saveIndex() {
      const indexRaw = toRaw(this.index.entries);

      // @ts-ignore
      window.chatIndexAPI.save(indexRaw);
      console.log('Index saved', indexRaw);
    },
    addIndexEntry(entry: IndexEntry) {
      // Insert entry at the start of the index
      this.index.entries.unshift(entry);
      this.saveIndex();
    },
    loadChatSession(sessionId: string) {
      // @ts-ignore
      const chatSession = window.chatAPI.loadChatSession(sessionId);
      console.log('Loaded chat session', chatSession);

      this.$patch({
        session: chatSession,
        chat: {configHeaderEnabled: false},
      });
    },
    async submitStreamPrompt(prompt: string) {
      return await chatRenderer.submit(prompt, this.session);
    },
    setDefaultPersona() {
      this.changePersona(DEFAULT_PERSONA.name);
    },
    changePersona(personaName: string) {

      console.log('Changing persona to', personaName);
      // @ts-ignore
      const persona = window.personaAPI.getPersona(personaName);
      if (persona) {
        this.session.persona = persona;
        console.log('Persona changed', persona);

        // @ts-ignore
        this.session.systemMessages = window.personaAPI.getSystemMessagesForPersona(persona);
      }
    },
    getPersonas() {
      // @ts-ignore
      return window.personaAPI.getPersonas();
    },
  },

});
