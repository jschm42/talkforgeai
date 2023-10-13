<template>
  <div class="container">
    <h2>Persona Editor</h2>
    <form class="form-panel" @submit.prevent="handleSubmit">

      <ul id="personaTab" class="nav nav-tabs" role="tablist">
        <li class="nav-item">
          <button id="home-tab" aria-controls="home-tab-pane" aria-selected="true" class="nav-link active"
                  data-bs-target="#main-tab-pane" data-bs-toggle="tab" role="tab" type="button">Home
          </button>
        </li>
        <li class="nav-item">
          <button id="model-tab" aria-controls="model-tab-pane" aria-selected="true" class="nav-link"
                  data-bs-target="#model-tab-pane" data-bs-toggle="tab" role="tab" type="button">Model
          </button>
        </li>
        <li class="nav-item">
          <button id="features-tab" aria-controls="features-tab-pane" aria-selected="true" class="nav-link"
                  data-bs-target="#features-tab-pane" data-bs-toggle="tab" role="tab" type="button">Features
          </button>
        </li>
      </ul>

      <div id="personaTabContent" class="tab-content">

        <div id="main-tab-pane" aria-labelledby="main-tab" class="tab-pane fade show active" role="tabpanel"
             tabindex="0">
          <persona-tab-main ref="mainTabPane"></persona-tab-main>

        </div>

        <div id="model-tab-pane" aria-labelledby="model-tab" class="tab-pane fade" role="tabpanel"
             tabindex="0">
          <h3>Model</h3>
        </div>

        <div id="features-tab-pane" aria-labelledby="features-tab" class="tab-pane fade" role="tabpanel"
             tabindex="0">
          <h3>Features</h3>
        </div>

      </div>

      <button class="btn btn-primary me-2" type="submit">Create persona</button>
      <button class="btn btn-secondary" type="button" @click.prevent="onCancel">Cancel</button>
    </form>
  </div>
</template>

<script>
import {defineComponent} from 'vue';
import PersonaTabMain from '@/components/persona/PersonaTabMain.vue';
import PersonaService from '@/service/persona.service';
import Persona from '@/store/to/persona';
import {usePersonaFormStore} from '@/store/persona-form-store';

const personaService = new PersonaService();

export default defineComponent({
  components: {PersonaTabMain},
  setup() {
    const store = usePersonaFormStore(); // Call useMyStore() inside the setup function
    return {store};
  },
  props: ['personaId'],
  methods: {
    async handleSubmit() {
      console.log('handleSubmit');

      const form = this.store.personaForm;
      const persona = new Persona();
      persona.personaId = form.personaId;
      persona.imagePath = 'cat.png';
      persona.name = form.name;
      persona.description = form.description;
      persona.system = form.system;
      persona.properties = {
        'chatgpt_model': 'gpt-4',
        'chatgpt_temperature': '0.7',
        'chatgpt_topP': '1.0',
        'elevenlabs_voiceId': '21m00Tcm4TlvDq8ikWAM',
        'elevenlabs_modelId': 'eleven_multilingual_v2',
      };

      try {
        await personaService.writePersona(persona);

        this.$router.push({name: 'persona-choice'});
      } catch (error) {
        console.error(error);
      }
    },
    onCancel() {
      console.log('onCancel');
      this.$router.push({name: 'persona-choice'});
    },
  },
  mounted() {
    this.store.resetPersonaEditForm();
    if (this.personaId) {
      personaService.readPersona(this.personaId).then((persona) => {
        console.log('Read persona', persona);

        this.store.setPersonaEditForm(persona);
      });
    }
  },
});
</script>

<style scoped>
.form-panel {
  color: white;
  text-align: left;
}

</style>
