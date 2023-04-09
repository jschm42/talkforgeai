import {reactive, readonly} from 'vue';
import ChatMessage from '../service/to/chat-message';
import Persona from '../service/to/persona';
import IndexEntry from '../service/to/index-entry';
import Role from '../service/to/role';

const state = reactive(
  {
    chat: {
      sessionId: '' as string,
      messages: [] as Array<ChatMessage>,
      processedMessages: [] as Array<ChatMessage>,
      persona: Persona,
    },
    index: {
      entries: [] as Array<IndexEntry>,
    },
  },
);

const getters = {};

const mutations = {
  chat: {
    submit(prompt: string) {
      console.log('submit', prompt);

      // @ts-ignore
      window.chatAPI.listenToPromptReply(({userMessage, originalAssistantMessage, processedAssistantMessage}) => {
        state.chat.processedMessages.push(userMessage);
        state.chat.messages.push(originalAssistantMessage);
        state.chat.processedMessages.push(processedAssistantMessage);
        console.log('Reply received', originalAssistantMessage, processedAssistantMessage);
      });

      const previousMessages = [];
      for (let i = 0; i < state.chat.messages.length; i++) {
        if (state.chat.messages[i].role === Role.USER) {
          previousMessages.push(Object.assign({}, state.chat.processedMessages[i]));
        } else {
          previousMessages.push(Object.assign({}, state.chat.messages[i]));
        }
      }

      state.chat.messages.push(new ChatMessage(Role.USER, prompt));

      // @ts-ignore
      window.chatAPI.submitPrompt(prompt, previousMessages);
    },
  },
  index: {
    load() {
      // @ts-ignore
      window.chatIndexAPI.listenToLoadReply((entries: Array<IndexEntry>) => {
        state.index.entries = [...entries];
        console.log('Index loaded', state.index.entries);
      });

      // @ts-ignore
      window.chatIndexAPI.load();
    },
    save() {
      // @ts-ignore
      window.chatIndexAPI.save(this.entries);
      console.log('Index saved', state.index.entries);
    },
    add(entry: IndexEntry) {
      state.index.entries.push(entry);
      this.save();
    },
  },

};

export default {
  state: readonly(state),
  getters,
  mutations,
};
