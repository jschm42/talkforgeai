import {defineStore} from 'pinia';
import ChatMessage from '../service/to/chat-message';
import IndexEntry from '../service/to/index-entry';
import {PERSONA} from '../service/to/persona';
import Role from '../service/to/role';

export const useChatStore = defineStore('chat', {
  state: () => {
    return {
      chat: {
        sessionId: '' as string,
        messages: [] as Array<ChatMessage>,
        processedMessages: [] as Array<ChatMessage>,
        personaName: '' as string,
      },
      index: {
        entries: [] as Array<IndexEntry>,
      },
      persona: PERSONA,
    };
  },
  actions: {
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
    submitPrompt(prompt: string) {
      console.log('submit', prompt);

      // @ts-ignore
      window.chatAPI.listenToPromptReply(({userMessage, originalAssistantMessage, processedAssistantMessage}) => {
        this.chat.processedMessages.push(userMessage);
        this.chat.messages.push(originalAssistantMessage);
        this.chat.processedMessages.push(processedAssistantMessage);
        console.log('Reply received', originalAssistantMessage, processedAssistantMessage);
      });

      const previousMessages = [];
      for (let i = 0; i < this.chat.messages.length; i++) {
        if (this.chat.messages[i].role === Role.USER) {
          previousMessages.push(Object.assign({}, this.chat.processedMessages[i]));
        } else {
          previousMessages.push(Object.assign({}, this.chat.messages[i]));
        }
      }

      this.chat.messages.push(new ChatMessage(Role.USER, prompt));

      // @ts-ignore
      window.chatAPI.submitPrompt(prompt, previousMessages);
    },
    changePersona(personaName: string) {
      const persona = PERSONA.find(p => p.name === personaName);
      if (persona) {
        // @ts-ignore
        window.chatAPI.setPersona(persona.name);
        console.log('Persona changed', persona);
      }
    },
  },

});
