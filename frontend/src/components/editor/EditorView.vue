<!--
  - Copyright (c) 2023-2024 Jean Schmitz.
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
  <div class="container col-lg-5 col-10 p-2 editor-container">
    <h2>Assistant Editor</h2>
    <form class="form-panel" @submit.prevent="handleSubmit">

      <ul id="personaTab" class="nav nav-tabs" role="tablist">
        <li class="nav-item">
          <button id="home-tab" aria-controls="home-tab-pane" aria-selected="true"
                  class="nav-link active"
                  data-bs-target="#main-tab-pane" data-bs-toggle="tab" role="tab" type="button">
            Profile
          </button>
        </li>
        <li class="nav-item">
          <button id="model-tab" aria-controls="model-tab-pane" aria-selected="true"
                  class="nav-link"
                  data-bs-target="#model-tab-pane" data-bs-toggle="tab" role="tab" type="button">
            Model
          </button>
        </li>
        <li class="nav-item">
          <button id="voice-tab" aria-controls="voice-tab-pane" aria-selected="true"
                  class="nav-link"
                  data-bs-target="#voice-tab-pane" data-bs-toggle="tab" role="tab" type="button">
            Voice
          </button>
        </li>
        <li class="nav-item">
          <button id="features-tab" aria-controls="features-tab-pane" aria-selected="true"
                  class="nav-link"
                  data-bs-target="#features-tab-pane" data-bs-toggle="tab" role="tab" type="button">
            Features
          </button>
        </li>
      </ul>

      <div id="personaTabContent" class="tab-content">

        <div id="main-tab-pane" aria-labelledby="main-tab" class="tab-pane fade show active"
             role="tabpanel"
             tabindex="0">
          <editor-tab-profile ref="mainTabPane"></editor-tab-profile>

        </div>

        <div id="model-tab-pane" aria-labelledby="model-tab" class="tab-pane fade" role="tabpanel"
             tabindex="0">
          <editor-tab-model ref="modelTabPane"></editor-tab-model>
        </div>

        <div id="voice-tab-pane" aria-labelledby="voice-tab" class="tab-pane fade" role="tabpanel"
             tabindex="0">
          <editor-tab-voice ref="voiceTabPane"></editor-tab-voice>
        </div>

        <div id="features-tab-pane" aria-labelledby="features-tab" class="tab-pane fade"
             role="tabpanel"
             tabindex="0">
          <editor-tab-features ref="featuresTabPane"></editor-tab-features>
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
import {usePersonaFormStore} from '@/store/persona-form-store';
import Assistant from '@/store/to/assistant';
import {useAppStore} from '@/store/app-store';
import EditorTabFeatures from '@/components/editor/EditorTabFeatures.vue';
import EditorTabVoice from '@/components/editor/EditorTabVoice.vue';
import EditorTabModel from '@/components/editor/EditorTabModel.vue';
import EditorTabProfile from '@/components/editor/EditorTabProfile.vue';
import QuestionModal from '@/components/common/QuestionModal.vue';
import {useAssistants} from '@/composable/use-assistants';

export default defineComponent({
  components: {
    EditorTabFeatures,
    EditorTabVoice,
    EditorTabModel,
    EditorTabProfile,
    QuestionModal,
  },
  setup() {
    const store = usePersonaFormStore(); // Call useMyStore() inside the setup function
    const appStore = useAppStore();
    const assistants = useAssistants();
    return {store, appStore, assistants};
  },
  data() {
    return {
      showModal: false,
    };
  },
  props: ['assistantId'],
  methods: {
    async handleSubmit() {
      const form = this.store.assistantForm;
      const assistant = new Assistant();
      assistant.image_path = form.image_path;
      assistant.id = undefined;
      assistant.instructions = form.instructions;
      assistant.name = form.name;
      assistant.description = form.description || '';
      assistant.model = form.model;
      assistant.tools = [];
      assistant.file_ids = [];
      assistant.metadata = {};
      assistant.properties = {...form.properties};

      try {
        if (this.assistantId) {
          await this.assistants.modifyAssistant(this.assistantId, assistant);
        } else {
          await this.assistants.createAssistant(assistant);
        }

        this.$router.push({name: 'persona-choice'});
      } catch (error) {
        this.appStore.handleError(error);
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
        try {
          this.assistants.deleteAssistant(this.assistantId).then(() => {
            this.$router.push({name: 'persona-choice'});
          });
        } catch (error) {
          this.appStore.handleError(error);
        }
      }
    },
  },
  async mounted() {
    this.store.resetPersonaEditForm();
    if (this.assistantId) {
      try {
        const assistant = await this.assistants.retrieveAssistant(this.assistantId);
        this.store.setPersonaEditForm(assistant);
      } catch (error) {
        this.appStore.handleError(error);
      }
    }
  },
});
</script>

<style scoped>
.form-panel {
  color: white;
  text-align: left;
}

.editor-container {
  max-height: 100vh;
  overflow-y: auto;
  overflow-x: hidden;
}

</style>
