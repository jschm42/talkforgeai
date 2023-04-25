import {defineStore} from 'pinia';
import IndexEntry from '../service/to/index-entry';
import {PERSONA} from '../service/to/persona';
import ChatSession from '../service/to/chat-session';
import {toRaw} from 'vue';
import ChatRendererOptimized from '../renderer/char.renderer.optimized';

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
      persona: PERSONA,
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
      this.index.entries.push(entry);
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
    changePersona(personaName: string) {
      const persona = PERSONA.find(p => p.name === personaName);
      if (persona) {
        this.session.personaName = personaName;
        this.session.system = persona.system;
        console.log('Persona changed', persona);
      }
    },
  },

});
