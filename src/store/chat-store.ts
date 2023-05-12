import {defineStore} from 'pinia';
import IndexEntry from '../service/to/index-entry';
import ChatSession from '../service/to/chat-session';
import {toRaw} from 'vue';
import ChatRendererOptimized from '../renderer/char.renderer.optimized';
import Persona, {DEFAULT_PERSONA, ElevenLabsProperties, IMAGE_GENERATION_SYSTEM} from '../service/to/persona';
import Role from '../service/to/role';
import ChatMessage from '../service/to/chat-message';
import IdentityUtil from '@/util/identity-util';

//const chatRenderer = new ChatRenderer();
const chatRenderer = new ChatRendererOptimized();

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
  },
  actions: {
    newSession() {
      /*
      this.$patch({
        session: new ChatSession(),
        chat: {configHeaderEnabled: true},
      });

       */
      this.session.sessionId = IdentityUtil.generateUUID();
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
    changePersona(personaName: string) {

      console.log('Changing persona to', personaName);
      // @ts-ignore
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
          this.session.systemMessages.push(new ChatMessage(Role.SYSTEM, IMAGE_GENERATION_SYSTEM));
        }

        this.session.systemMessages.push(new ChatMessage(Role.SYSTEM, foundPersona.system));
        console.log('Persona changed', foundPersona);

        // @ts-ignore
        //this.session.systemMessages = window.personaAPI.getSystemMessagesForPersona(persona);
      } else {
        console.error('Persona ' + personaName + ' not found.');
      }
    },
    async readPersonas() {
      // @ts-ignore
      this.persona = await window.personaAPI.readPersonas();
    },
    getElevenLabsProperties(): ElevenLabsProperties {
      return this.session.persona.elevenLabsProperties;
    },
    toggleAutoSpeak() {
      this.chat.autoSpeak = !this.chat.autoSpeak;
    },
  },

});
