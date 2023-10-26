<script>
import {defineComponent} from 'vue';
import {storeToRefs} from 'pinia';
import {usePersonaFormStore} from '@/store/persona-form-store';
import axios from 'axios';
import PersonaService from '@/service/persona.service';

const personaService = new PersonaService();

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
    const {personaForm} = storeToRefs(usePersonaFormStore());

    return {personaForm};
  },
  methods: {
    getImageUrl(fileName) {
      return '/api/v1/persona/image/' + fileName;
    },
    async onFileSelected(event) {
      const selectedFile = event.target.files[0];
      console.log('Selected file:', selectedFile);

      const uploadedFileName = await this.uploadImage(selectedFile);
      console.log('Uploaded file:', uploadedFileName.data);

      this.$refs.fileInput.textContent = uploadedFileName.data.filename;

      this.personaForm.imagePath = uploadedFileName.data.filename;
    },
    async uploadImage(file) {
      const formData = new FormData();
      formData.append('file', file);

      try {
        return await axios.post('/api/v1/persona/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });
      } catch (error) {
        console.log('Failed to upload image: ', error);
      }
    },
    triggerFileInput() {
      this.$refs.fileInput.click();
    },
    getAltImageText() {
      return this.personaForm.imagePath;
    },
    async onGenerateImage() {
      this.isGenerationRunning = true;
      const imageResponse = await personaService.generatePersonaImage(this.imageGenerationPrompt);
      console.log('Image response: ', imageResponse);
      this.personaForm.imagePath = imageResponse.data.fileName;
      this.showModal = false;
      this.isGenerationRunning = false;
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
        <div v-if="!personaForm.imagePath"
             class="placeholder-image img-thumbnail d-flex justify-content-center align-items-center" role="button"
             @click="triggerFileInput">
          <i class="bi bi-person"></i>
        </div>
        <img v-else :alt="personaForm.imagePath" :src="getImageUrl(personaForm.imagePath)"
             :title="personaForm.imagePath"
             class="img-thumbnail thumbnail-image"
             role="button" @click="triggerFileInput"/>
        <input id="personaImage" ref="fileInput" class=" col-10 form-control" style="display: none" type="file"
               @change="onFileSelected">
      </div>
    </div>
    <div class="row">
      <div class="col-5">
        <button class="btn btn-primary my-2" type="button" @click.prevent="onShowGenerationModal">
          <i class="bi bi-magic mx-2"></i>Generate...
        </button>
      </div>
    </div>
  </div>

  <div class="mb-3">
    <label class="form-label" for="personaName">Name</label>
    <input id="personaName" v-model="personaForm.name" class="form-control" maxlength="32" required
           type="text">
  </div>
  <div class="mb-3">
    <label class="form-label" for="personaDescription">Description</label>
    <textarea id="personaDescription" v-model="personaForm.description" class="form-control"
              maxlength="256"
              rows="4"></textarea>
  </div>
  <div class="mb-3">
    <label class="form-label" for="personaSystem">System</label>
    <textarea id="personaSystem" v-model="personaForm.system" class="form-control" maxlength="16384"
              rows="10"></textarea>
  </div>

  <!-- The Modal -->
  <div v-if="showModal" class="modal" role="dialog" tabindex="-1">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Generate persona image</h5>
          <button :disabled="isGenerationRunning" aria-label="Close" class="close" data-dismiss="modal" type="button"
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
            <span v-if="isGenerationRunning" aria-hidden="true" class="spinner-border spinner-border-sm mx-2"
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
