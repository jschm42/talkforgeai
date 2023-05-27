import {defineStore} from 'pinia';
import {toRaw} from 'vue';
import ChatSession from '@/store/to/chat-session';
import IndexEntry from '@/store/to/index-entry';
import ChatMessage from '@/store/to/chat-message';
import Role from '@/store/to/role';
import Persona, {
  DEFAULT_PERSONA,
  ElevenLabsProperties,
  IMAGE_GENERATION_SYSTEM,
} from '@/store/to/persona';

//const chatRenderer = new ChatRenderer();

export const useChatStore = defineStore('chat', {
  state: () => {
    return {
      session: new ChatSession(),
      chat: {
        configHeaderEnabled: true,
        autoSpeak: false,
      },
      persona: [] as Array<Persona>,
      index: {
        entries: [] as Array<IndexEntry>,
      },
    };
  },
  getters: {
    autoSpeak(): boolean {
      return this.chat.autoSpeak;
    },
    isEmptySession(): boolean {
      return this.session.messages.length == 0;
    },
  },
  actions: {
    newSession() {
      /*
      this.$patch({
        session: new ChatSession(),
        chat: {configHeaderEnabled: true},
      });

       */
      this.session.sessionId = '';
      this.session.messages = [];
      this.session.processedMessages = [];
      this.session.systemMessages = [];
      this.session.persona = DEFAULT_PERSONA;
      this.chat.configHeaderEnabled = true;
    },
    disableConfigHeader() {
      console.log('disableConfigHeader');
      this.chat.configHeaderEnabled = false;
    },
    loadIndex() {
      //const entries = window.chatIndexAPI.load();
      //console.log('Index entries loaded', entries);
      //this.index.entries = entries;
    },
    saveIndex() {
      const indexRaw = toRaw(this.index.entries);

      //window.chatIndexAPI.save(indexRaw);
      console.log('Index saved', indexRaw);
    },
    addIndexEntry(entry: IndexEntry) {
      // Insert entry at the start of the index
      this.index.entries.unshift(entry);
      this.saveIndex();
    },
    loadChatSession(sessionId: string) {
      //const chatSession = window.chatAPI.loadChatSession(sessionId);
      //console.log('Loaded chat session with ID: ' + sessionId, chatSession);

      this.$patch({
        //session: chatSession,
        chat: {configHeaderEnabled: false},
      });
    },
    async submitPrompt(prompt: string) {
      //return await chatRenderer.submit(prompt, this.session);
    },
    changePersona(personaName: string) {

      console.log('Changing persona to', personaName);
      //const persona = window.personaAPI.getPersona(personaName);
      //const persona = <Persona>this.persona.filter(p => p.name === personaName);
      let foundPersona;
      for (const per of this.persona) {
        if (per.name === personaName) {
          foundPersona = per;
          break;
        }
      }

      console.log('FOUND', foundPersona);
      if (foundPersona) {
        this.session.persona = foundPersona;

        if (this.session.persona.withImagePromptSystem) {
          this.session.systemMessages.push(
              new ChatMessage(Role.SYSTEM, IMAGE_GENERATION_SYSTEM));
        }

        this.session.systemMessages.push(
            new ChatMessage(Role.SYSTEM, foundPersona.system));
        console.log('Persona changed', foundPersona);

        //this.session.systemMessages = window.personaAPI.getSystemMessagesForPersona(persona);
      } else {
        console.error('Persona ' + personaName + ' not found.');
      }
    },
    async readPersonas() {
      //this.persona = await window.personaAPI.readPersonas();
    },
    getElevenLabsProperties(): ElevenLabsProperties {
      return this.session.persona.elevenLabsProperties;
    },
    toggleAutoSpeak() {
      this.chat.autoSpeak = !this.chat.autoSpeak;
    },
  },

});
