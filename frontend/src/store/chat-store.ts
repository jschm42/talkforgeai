import {defineStore} from 'pinia';
import Session from '@/store/to/session';
import ChatMessage from '@/store/to/chat-message';
import Persona from '@/store/to/persona';
import ChatService from '@/service/chat.service';
import PersonaService from '@/service/persona.service';
import Role from '@/store/to/role';
import ChatStreamService from '@/service/chat-stream.service';
import HighlightingService from '@/service/highlighting.service';

const chatService = new ChatService();
const chatStreamService = new ChatStreamService();
const personaService = new PersonaService();
const highlightingService = new HighlightingService();

export const useChatStore = defineStore('chat', {
  state: () => {
    return {
      sessionId: '',
      messages: [] as Array<ChatMessage>,
      selectedPersona: {} as Persona,
      personaList: [] as Array<Persona>,
      chat: {
        locked: false,
        configHeaderEnabled: true,
        autoSpeak: false,
      },
      currentStatusMessage: '',
      currentStatusMessageType: '',
      sessions: [] as Array<Session>,
      selectedSessionId: '',
    };
  },
  getters: {
    autoSpeak(): boolean {
      return this.chat.autoSpeak;
    },
    isEmptySession(): boolean {
      return this.messages.length == 0;
    },
    maxMessageIndex(): number {
      return this.messages.length - 1;
    },
  },
  actions: {
    newSession() {
      this.$patch({
        sessionId: '',
        messages: [],
        chat: {
          configHeaderEnabled: true,
        },
        currentStatusMessage: '',
        selectedSessionId: '',
      });
    },
    resetChat() {
      this.$patch({
        sessionId: '',
        messages: [],
        currentStatusMessage: '',
        selectedSessionId: '',
        sessions: [],
        chat: {
          autoSpeak: false,
          configHeaderEnabled: true,
        },
      });
    },
    async selectPersona(persona: Persona):Promise<void> {
      this.resetChat();
      this.selectedPersona = persona;
      await this.loadIndex(persona.personaId);
    },
    async selectPersonaById(personaId: string):Promise<void> {
      const persona = this.personaList.find(p => p.personaId === personaId);
      console.log('selectedPersona', persona);
      if (persona) {
        await this.selectPersona(persona);
      }
    },
    async loadIndex(personaId: string):Promise<void> {
      this.sessions = await chatService.readSessionEntries(personaId);
    },
    async streamPrompt(prompt: string,  chunkUpdateCallback: () => void) {
      this.chat.configHeaderEnabled = false;

      if (!this.sessionId || this.sessionId === '') {
        this.sessionId = await chatService.createNewSession(this.selectedPersona.personaId);
      }

      this.selectedSessionId = this.sessionId;

      this.messages.push(new ChatMessage(Role.USER, prompt));
      chunkUpdateCallback();

      console.log('Submitting prompt', this.sessionId, prompt);

      await chatStreamService.streamSubmit(this.sessionId,  prompt, chunkUpdateCallback);
      await this.generateSessionTitle(this.sessionId);
      await this.loadIndex(this.selectedPersona.personaId);
    },
    async loadChatSession(sessionId: string) {
      const chatSession = await chatService.readSessionEntry(sessionId);

      highlightingService.highlightCodeInChatMessage(chatSession.chatMessages);

      this.$patch({
        sessionId: chatSession.id,
        messages: chatSession.chatMessages,
        selectedPersona: chatSession.persona,
        chat: {configHeaderEnabled: false},
      });

    },
    async updateSessionTitle(sessionId: string, newTitle: string) {
      await chatService.updateSessionTitle(sessionId, newTitle);
    },
    hasEmptySessionTitle(sessionId: string) {
      const currentSession = this.sessions.find(s => s.id === sessionId);
      console.log('currentSession', currentSession);
      if (currentSession) {
        return currentSession.title === '' || currentSession.title === undefined || currentSession.title === '<empty>';
      }
      return true;
    },
    async generateSessionTitle(sessionId: string) {
      const userMessage = this.messages.find(m => m.role === Role.USER);
      const userMessageContent = userMessage ? userMessage.content : '';

      const assistantMessage = this.messages.find(m => m.role === Role.ASSISTANT);
      const assistantMessageContent = assistantMessage ? assistantMessage.content : '';

      if (this.hasEmptySessionTitle(sessionId)) {
        this.updateStatus('Generating title...', 'running');

        try {
          const response = await chatService.generateSessionTitle(sessionId, userMessageContent,
            assistantMessageContent);
          console.log('Generated title response', response);
          if (response) {
            const currentSession = this.sessions.find(s => s.id === this.selectedSessionId);
            if (currentSession) {
              currentSession.title = response.generatedTitle;
            }
          }
        } finally {
          this.updateStatus('');
        }
      } else {
        console.log('Session title already set. Skipping generation.');
      }
    },
    async deleteChatSession(sessionId: string) {
      await chatService.deleteSession(this.sessionId);
      this.selectedSessionId = '';

      const foundSession = this.sessions.find((e) => e.id === sessionId);
      if (foundSession) {
        this.sessions.splice(this.sessions.indexOf(foundSession), 1);
      }

      if (this.sessions.length > 0) {
        this.selectedSessionId = this.sessions[0].id;
        await this.loadChatSession(this.selectedSessionId);
      } else {
        this.resetChat();
      }

    },
    async readPersona() {
      this.personaList = await personaService.readPersona();
      this.selectedPersona = this.personaList[0];
    },
    toggleAutoSpeak() {
      this.chat.autoSpeak = !this.chat.autoSpeak;
    },
    updateStatus(message: string, type = '') {
      this.currentStatusMessage = message;
      this.currentStatusMessageType = type;
    },
    removeStatus() {
      this.currentStatusMessage = '';
      this.currentStatusMessageType = '';
    },
  },

});
