<!--
  - Copyright (c) 2023 Jean Schmitz.
  -
  - Licensed under the Apache License, Version 2.0 (the "License");
  - you may not use this file except in compliance with the License.
  - You may obtain a copy of the License at
  -
  -     http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing, software
  - distributed under the License is distributed on an "AS IS" BASIS,
  - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  - See the License for the specific language governing permissions and
  - limitations under the License.
  -->

<template>
  <div class="container col-lg-5 col-10">
    <h2>Persona Editor</h2>
    <form class="form-panel" @submit.prevent="handleSubmit">

      <ul id="personaTab" class="nav nav-tabs" role="tablist">
        <li class="nav-item">
          <button id="home-tab" aria-controls="home-tab-pane" aria-selected="true" class="nav-link active"
                  data-bs-target="#main-tab-pane" data-bs-toggle="tab" role="tab" type="button">Profile
          </button>
        </li>
        <li class="nav-item">
          <button id="model-tab" aria-controls="model-tab-pane" aria-selected="true" class="nav-link"
                  data-bs-target="#model-tab-pane" data-bs-toggle="tab" role="tab" type="button">Model
          </button>
        </li>
        <li class="nav-item">
          <button id="voice-tab" aria-controls="voice-tab-pane" aria-selected="true" class="nav-link"
                  data-bs-target="#voice-tab-pane" data-bs-toggle="tab" role="tab" type="button">Voice
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
          <persona-tab-profile ref="mainTabPane"></persona-tab-profile>

        </div>

        <div id="model-tab-pane" aria-labelledby="model-tab" class="tab-pane fade" role="tabpanel"
             tabindex="0">
          <persona-tab-model ref="modelTabPane"></persona-tab-model>
        </div>

        <div id="voice-tab-pane" aria-labelledby="voice-tab" class="tab-pane fade" role="tabpanel"
             tabindex="0">
          <persona-tab-voice ref="voiceTabPane"></persona-tab-voice>
        </div>

        <div id="features-tab-pane" aria-labelledby="features-tab" class="tab-pane fade" role="tabpanel"
             tabindex="0">
          <persona-tab-features ref="featuresTabPane"></persona-tab-features>
        </div>

      </div>

      <button class="btn btn-primary me-2" type="submit">Save</button>
      <button class="btn btn-secondary me-2" @click.prevent="onDelete">Delete</button>
      <button class="btn btn-secondary" type="button" @click.prevent="onCancel">Cancel</button>
    </form>
  </div>

  <QuestionModal
    :isOpen="showModal"
    message="Are you sure you want to delete this persona?"
    title="Delete Persona"
    @answer="handleDeleteQuestionAnswer"
  />
</template>

<script>
import {defineComponent} from 'vue';
import PersonaService from '@/service/persona.service';
import Persona from '@/store/to/persona';
import {usePersonaFormStore} from '@/store/persona-form-store';
import PersonaTabModel from '@/components/persona/PersonaTabModel.vue';
import PersonaTabProfile from '@/components/persona/PersonaTabProfile.vue';
import PersonaTabVoice from '@/components/persona/PersonaTabVoice.vue';
import PersonaTabFeatures from '@/components/persona/PersonaTabFeatures.vue';
import QuestionModal from '@/components/QuestionModal.vue';

const personaService = new PersonaService();

export default defineComponent({
  components: {PersonaTabFeatures, PersonaTabVoice, PersonaTabModel, PersonaTabProfile, QuestionModal},
  setup() {
    const store = usePersonaFormStore(); // Call useMyStore() inside the setup function
    return {store};
  },
  data() {
    return {
      showModal: false,
    };
  },
  props: ['personaId'],
  methods: {
    async handleSubmit() {
      console.log('handleSubmit');

      const form = this.store.personaForm;
      const persona = new Persona();
      persona.personaId = form.personaId;
      persona.imagePath = form.imagePath;
      persona.name = form.name;
      persona.description = form.description;
      persona.system = form.system;
      persona.properties = form.properties;

      try {
        await personaService.writePersona(persona);

        this.$router.push({name: 'persona-choice'});
      } catch (error) {
        console.error(error);
      }
    },
    onCancel() {
      this.$router.push({name: 'persona-choice'});
    },
    onDelete() {
      this.showModal = true;
    },
    handleDeleteQuestionAnswer(answer) {
      this.showModal = false;
      if (answer) {
        personaService.deletePersona(this.store.personaForm.personaId).then(() => {
          this.$router.push({name: 'persona-choice'});
        });
      }
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
