import {defineStore} from 'pinia';
import Persona from '@/store/to/persona';
import ChatService from '@/service/chat.service';
import PersonaService from '@/service/persona.service';
import ChatStreamService from '@/service/chat-stream.service';
import HighlightingService from '@/service/highlighting.service';

const chatService = new ChatService();
const chatStreamService = new ChatStreamService();
const personaService = new PersonaService();
const highlightingService = new HighlightingService();

export const usePersonaFormStore = defineStore('personaForm', {
  state: () => {
    return {
      form: {} as Persona,
    };
  },
  getters: {
    personaForm(): Persona {
      return this.form;
    },
  },
  actions: {
    updatePersonaEditForm(persona: Persona) {
      this.form = persona;
    },
    resetPersonaEditForm() {
      this.form = new Persona();
    },
    setPersonaEditForm(persona: Persona) {
      this.form = persona;
    },
  },

});
