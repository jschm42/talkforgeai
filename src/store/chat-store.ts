import {defineStore} from 'pinia';
import ChatMessage from '../service/to/chat-message';
import IndexEntry from '../service/to/index-entry';
import {PERSONA} from '../service/to/persona';
import Role from '../service/to/role';
import ChatSession from '../service/to/chat-session';
import {toRaw} from 'vue';
import OpenAiRenderer from '../renderer/openai.renderer';
import ChatRenderer from '../renderer/chat.renderer';

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
    addToLastMessage(content: string) {
      const lastMessage = this.session.processedMessages[this.session.processedMessages.length - 1];
      lastMessage.content += content;
    },
    async submitStreamPrompt(prompt: string) {
      const openAiSerice = new OpenAiRenderer();
      const chatRenderer = new ChatRenderer();

      const previousMessages = chatRenderer.getPreviousMessages(this.session.system, this.session.messages,
        this.session.processedMessages);

      const userMessage = new ChatMessage(Role.USER, prompt);
      this.session.messages.push(userMessage);

      const submitMessages = [...previousMessages, userMessage];

      // @ts-ignore
      const response = await openAiSerice.chatCompletion(submitMessages, true);
      const reader = response.body.getReader();

      const processedMessage = new ChatMessage(Role.ASSISTANT, '');
      this.session.processedMessages.push(processedMessage);

      // @ts-ignore
      let done = false;
      let command = '';
      let commandMode = false;

      while (!done) {
        const row = await reader.read();
        const str = new TextDecoder().decode(row.value);
        const parsed = chatRenderer.parseStreamResponse(str);

        const contentArray = parsed.filter(e => e.type === 'content').map(e => e.value);

        for (let value of contentArray) {
          console.log('COMMAND MODE', commandMode);
          if (value) {

            if (value === '``') {
              if (commandMode) {
                console.log('TURN COMMAND MODE OFF');
                commandMode = false;
                console.log('COMMAND FOUND: ', command);
              } else {
                console.log('TURN COMMAND MODE ON');
                commandMode = true;
                command = '';
              }
            }

            if (commandMode) {
              command += value;
              console.log('CUR COMMAND VALUE', command);
            } else {
              value = value.replace('\\n\\n', '<p/>');

              console.log('CUR VALUE', value);
              this.addToLastMessage(value);
            }

          }
        }

        done = row.done;
      }
      console.log('Stream complete');

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
