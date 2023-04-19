import {defineStore} from 'pinia';
import ChatMessage from '../service/to/chat-message';
import IndexEntry from '../service/to/index-entry';
import {DEFAULT_PERSONA, PERSONA} from '../service/to/persona';
import Role from '../service/to/role';
import ChatSession from '../service/to/chat-session';
import {toRaw} from 'vue';

export const useChatStore = defineStore('chat', {
  state: () => {
    return {
      chat: {
        sessionId: '' as string,
        messages: [] as Array<ChatMessage>,
        processedMessages: [] as Array<ChatMessage>,
        personaName: DEFAULT_PERSONA.name,
        system: DEFAULT_PERSONA.system,
        configHeaderEnabled: true,
      },
      index: {
        entries: [] as Array<IndexEntry>,
      },
      persona: PERSONA,
    };
  },
  actions: {
    disableConfigHeader() {
      console.log('disableConfigHeader');
      this.chat.configHeaderEnabled = false;
    },
    loadIndex() {
      // @ts-ignore
      window.chatIndexAPI.listenToLoadReply((entries: Array<IndexEntry>) => {
        this.index.entries = [...entries];
        console.log('Index loaded', this.index.entries);
      });

      // @ts-ignore
      window.chatIndexAPI.load();
    },
    saveIndex() {
      // @ts-ignore
      window.chatIndexAPI.save(this.entries);
      console.log('Index saved', this.index.entries);
    },
    addIndexEntry(entry: IndexEntry) {
      this.index.entries.push(entry);
      this.saveIndex();
    },
    loadChatSession(sessionId: string) {
      // @ts-ignore
      window.chatIndexAPI.listenToLoadChatSessionReply((chatSession: ChatSession) => {
        this.chat.sessionId = chatSession.sessionId;
        this.chat.messages = chatSession.messages;
        this.chat.processedMessages = chatSession.processedMessages;
      });

      // @ts-ignore
      window.chatAPI.loadChatSession(sessionId);
    },
    async submitPrompt(prompt: string) {
      console.log('submit', prompt);

      /*
      // @ts-ignore
      window.chatAPI.listenToPromptReply(({userMessage, originalAssistantMessage, processedAssistantMessage}) => {
        this.chat.processedMessages.push(userMessage);
        this.chat.messages.push(originalAssistantMessage);
        this.chat.processedMessages.push(processedAssistantMessage);
        console.log('Reply received', originalAssistantMessage, processedAssistantMessage);

        const chatSession = new ChatSession();
        chatSession.sessionId = this.chat.sessionId;
        chatSession.processedMessages = this.chat.processedMessages;
        chatSession.messages = this.chat.messages;
        // @ts-ignore
        window.chatAPI.writeChatSession(chatSession);
      });

       */

      const previousMessages = [];
      previousMessages.push(new ChatMessage(Role.SYSTEM, this.chat.system));

      for (let i = 0; i < this.chat.messages.length; i++) {
        if (this.chat.messages[i].role === Role.USER) {
          previousMessages.push(Object.assign({}, this.chat.processedMessages[i]));
        } else {
          previousMessages.push(Object.assign({}, this.chat.messages[i]));
        }
      }

      this.chat.messages.push(new ChatMessage(Role.USER, prompt));

      // @ts-ignore
      const result = await window.chatAPI.submitPrompt(prompt, previousMessages);
      console.log('Submit Result', result);

      this.chat.processedMessages.push(result.userMessage);
      this.chat.messages.push(result.originalAssistantMessage);
      this.chat.processedMessages.push(result.processedAssistantMessage);

      const chatSession = new ChatSession();
      chatSession.sessionId = toRaw(this.chat.sessionId);
      chatSession.messages = toRaw(this.chat.messages);
      chatSession.processedMessages = toRaw(this.chat.processedMessages);
      // @ts-ignore
      window.chatAPI.writeChatSession(chatSession);

      /*
      window.chatAPI.submitPrompt(prompt, previousMessages).then(result => {


        this.chat.processedMessages.push(result.userMessage);
        this.chat.messages.push(result.originalAssistantMessage);
        this.chat.processedMessages.push(result.processedAssistantMessage);
        console.log('Reply received', result);
      }).catch((error: any) => {
        console.log('ERROR', error);
      });

       */

    },
    changePersona(personaName: string) {
      const persona = PERSONA.find(p => p.name === personaName);
      if (persona) {
        this.chat.personaName = personaName;
        this.chat.system = persona.system;
        console.log('Persona changed', persona);
      }
    },
  },

});
