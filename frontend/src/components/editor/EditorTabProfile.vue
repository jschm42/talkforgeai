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

<script>
import {defineComponent} from 'vue';
import {storeToRefs} from 'pinia';
import {usePersonaFormStore} from '@/store/persona-form-store';
import {useChatStore} from '@/store/chat-store';
import {useAppStore} from '@/store/app-store';
import {useAssistants} from '@/composable/use-assistants';

export default defineComponent({
  name: 'PersonaTabProfile',
  data() {
    return {
      imageGenerationPrompt: '',
      isGenerationRunning: false,
      showModal: false,
    };
  },
  setup() {
    const chatStore = useChatStore();
    const appStore = useAppStore();
    const assistants = useAssistants();
    const {assistantForm} = storeToRefs(usePersonaFormStore());

    return {assistantForm, chatStore, appStore, assistants};
  },
  methods: {
    getImageUrl(fileName) {
      return this.assistants.getAssistantImageUrl(fileName);
    },
    async onFileSelected(event) {
      const selectedFile = event.target.files[0];
      console.log('Selected file:', selectedFile);

      try {
        const uploadedFileName = await this.assistants.uploadAssistantImage(selectedFile);
        if (uploadedFileName) {
          console.log('Uploaded file:', uploadedFileName.data);
          this.$refs.fileInput.textContent = uploadedFileName.data.filename;
          this.assistantForm.imagePath = uploadedFileName.data.filename;
        }
      } catch (error) {
        this.appStore.handleError(error);
      }
    },
    triggerFileInput() {
      this.$refs.fileInput.click();
    },
    getAltImageText() {
      return this.assistantForm.imagePath;
    },
    async onGenerateImage() {
      this.isGenerationRunning = true;
      try {
        const imageResponse = await this.assistants.generateAssistantImage(
            this.imageGenerationPrompt);
        console.log('Image response: ', imageResponse);
        this.assistantForm.imagePath = imageResponse.data.fileName;
        this.showModal = false;
        this.isGenerationRunning = false;
      } catch (error) {
        this.appStore.handleError(error);
        this.isGenerationRunning = false;
      }
    },
    async onAutoGenerateImage() {
      this.isGenerationRunning = true;
      try {
        const prompt = 'Profile picture of: ' + this.assistantForm.name;
        const imageResponse = await this.assistants.generateAssistantImage(prompt);
        this.assistantForm.imagePath = imageResponse.data.fileName;
        this.showModal = false;
        this.isGenerationRunning = false;
      } catch (error) {
        this.appStore.handleError(error);
        this.isGenerationRunning = false;
      }
    },
    onShowGenerationModal() {
      this.showModal = true;
    },
  },
});
</script>

<template>
  <div class="mb-3">
    <div class="row">
      <div class="col-4">
        <div v-if="!assistantForm.imagePath"
             class="placeholder-image img-thumbnail d-flex justify-content-center align-items-center"
             role="button"
             @click="triggerFileInput">
          <i class="bi bi-person"></i>
        </div>
        <img v-else :alt="assistantForm.imagePath" :src="getImageUrl(assistantForm.imagePath)"
             :title="assistantForm.imagePath"
             class="img-thumbnail thumbnail-image"
             role="button" @click="triggerFileInput"/>
        <input id="personaImage" ref="fileInput" :disabled="isGenerationRunning"
               class=" col-10 form-control"
               style="display: none"
               type="file" @change="onFileSelected">
      </div>
    </div>
    <div class="row">
      <div class="col">
        <button :disabled="isGenerationRunning" class="btn btn-primary my-2" type="button"
                @click.prevent="onShowGenerationModal">
          <i class="mdi mdi-text mx-2"></i>Generate with prompt...
        </button>
        <button :disabled="isGenerationRunning" class="btn btn-primary mx-2" type="button"
                @click.prevent="onAutoGenerateImage">
          <span v-if="!isGenerationRunning"><i
              class="mdi mdi-auto-fix mx-2"></i>Auto Generate</span>
          <span v-if="isGenerationRunning"><span aria-hidden="true"
                                                 class="spinner-border spinner-border-sm mx-2"
                                                 role="status"></span>Auto Generate</span>
        </button>
      </div>
    </div>
  </div>

  <div class="mb-3">
    <label class="form-label" for="personaName">Name</label>
    <input id="personaName" v-model="assistantForm.name" :disabled="isGenerationRunning"
           class="form-control"
           maxlength="32"
           required type="text">
  </div>
  <div class="mb-3">
    <label class="form-label" for="personaDescription">Description (will not be used in generation
      requests)</label>
    <textarea id="personaDescription" v-model="assistantForm.description"
              :disabled="isGenerationRunning"
              class="form-control"
              maxlength="256" rows="2"></textarea>
  </div>
  <div class="mb-3">
    <label class="form-label" for="personaPersonality">Describe the persona's character traits. How
      would you prefer it
      to respond?</label>
    <textarea id="personaPersonality" v-model="assistantForm.instructions"
              :disabled="isGenerationRunning"
              class="form-control"
              maxlength="16384" rows="5"></textarea>
  </div>

  <!-- The Modal -->
  <div v-if="showModal" class="modal" role="dialog" tabindex="-1">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Generate persona image</h5>
          <button :disabled="isGenerationRunning" aria-label="Close" class="close"
                  data-dismiss="modal" type="button"
                  @click="showModal = false">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label" for="imageGenPrompt">Image generation prompt</label>
            <textarea id="imageGenPrompt" v-model="imageGenerationPrompt"
                      :disabled="isGenerationRunning" class="form-control" maxlength="2048"
                      required
                      rows="4"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button :disabled="isGenerationRunning" class="btn btn-secondary" data-dismiss="modal"
                  type="button"
                  @click="showModal = false">Close
          </button>
          <button :disabled="isGenerationRunning" class="btn btn-primary" type="button"
                  @click.prevent="onGenerateImage">
            <span v-if="isGenerationRunning" aria-hidden="true"
                  class="spinner-border spinner-border-sm mx-2"
                  role="status"></span>
            <span v-if="isGenerationRunning">Generating...</span>
            <span v-else>Generate</span>
          </button>
        </div>
      </div>
    </div>
  </div>


</template>

<style scoped>
.placeholder-image {
  font-size: 7em;
  width: 200px;
  height: 200px;
}

.thumbnail-image {
  min-width: 200px;
  min-height: 200px;
  width: 200px;
  height: 200px;
}

.modal {
  display: block;
  background-color: rgba(0, 0, 0, 0.5);
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1050;
  overflow: auto;
}

</style>
