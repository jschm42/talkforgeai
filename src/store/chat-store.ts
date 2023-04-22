import {defineStore} from 'pinia';
import ChatMessage from '../service/to/chat-message';
import IndexEntry from '../service/to/index-entry';
import {PERSONA} from '../service/to/persona';
import Role from '../service/to/role';
import ChatSession from '../service/to/chat-session';
import {toRaw} from 'vue';
import OpenAiRenderer from "../service/openai.renderer";

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

      console.log('submit', prompt);

      const previousMessages = [];
      previousMessages.push(new ChatMessage(Role.SYSTEM, this.session.system));

      for (let i = 0; i < this.session.messages.length; i++) {
        if (this.session.messages[i].role === Role.USER) {
          previousMessages.push(Object.assign({}, this.session.processedMessages[i]));
        } else {
          previousMessages.push(Object.assign({}, this.session.messages[i]));
        }
      }

      const userMessage = new ChatMessage(Role.USER, prompt);
      this.session.messages.push(userMessage);

      const submitMessages = [...previousMessages, userMessage];
      console.log("Submitt messages", submitMessages);

      // @ts-ignore
      const response = await openAiSerice.chatCompletion(submitMessages, true);
      const reader = response.body.getReader();

      const processedMessage = new ChatMessage(Role.ASSISTANT, '');
      this.session.processedMessages.push(processedMessage);

      // @ts-ignore
      let done = false;
      while (!done) {
        const row = await reader.read();

        const regex = /"delta":\s*{"(role|content)":"([^"]+)"/;

        const str = new TextDecoder().decode(row.value);

        const parsed = str.split("\n\n")
          .filter(e => e.length > 0)
          .map(e => regex.exec(e))
          .map(p => {
            if (p === null) return {}
            return {
              type: p[1], value: p[2]
            }
          });

        parsed.filter(e => e.type === 'content')
          .forEach(p => {
            console.log("Adding content", p.value);


            if (p.value) {
              p.value = p.value.replace('\\n\\n', '<p/>');
              this.addToLastMessage(p.value);
            }
          })

        console.log("Decoded Text", parsed);

        done = row.done;
      }
      console.log('Stream complete');


      /*
            const isFirstSubmit = this.session.processedMessages.length == 0;
            if (isFirstSubmit) {
              this.addIndexEntry(new IndexEntry(this.session.sessionId, prompt, 'Description', new Date()));
            }

            this.session.processedMessages.push(result.userMessage);
            this.session.messages.push(result.originalAssistantMessage);
            this.session.processedMessages.push(result.processedAssistantMessage);

            // @ts-ignore
            window.chatAPI.writeChatSession(toRaw(this.session));
            */
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
