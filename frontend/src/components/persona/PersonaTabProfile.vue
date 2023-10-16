<script>
import {defineComponent} from 'vue';
import {storeToRefs} from 'pinia';
import {usePersonaFormStore} from '@/store/persona-form-store';
import axios from 'axios';

export default defineComponent({
  name: 'PersonaTabProfile',
  data() {
    return {
      // selectedFile: null,
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
  },
});
</script>

<template>
  <div class="mb-3">
    <div>
      <div v-if="!personaForm.imagePath"
           class="placeholder-image img-thumbnail d-flex justify-content-center align-items-center" role="button"
           @click="triggerFileInput">
        <i class="bi bi-person"></i>
      </div>
      <img v-else :alt="personaForm.imagePath" :src="getImageUrl(personaForm.imagePath)" :title="personaForm.imagePath"
           class="img-thumbnail thumbnail-image"
           role="button" @click="triggerFileInput"/>
    </div>
    <input id="personaImage" ref="fileInput" class=" col-10 form-control" style="display: none" type="file"
           @change="onFileSelected">
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

</style>
