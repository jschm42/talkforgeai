import {defineStore} from 'pinia';
import Persona from '@/store/to/persona';

export const usePersonaFormStore = defineStore('personaForm', {
  state: () => {
    return {
      form: new Persona(),
    };
  },
  getters: {
    personaForm(): Persona {
      return this.form;
    },
  },
  actions: {
    resetPersonaEditForm() {
      this.$reset();
    },
    setPersonaEditForm(persona: Persona) {
      this.form = {...persona};
    },
  },

});
