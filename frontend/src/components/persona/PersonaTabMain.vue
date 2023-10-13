<script>
import {defineComponent} from 'vue';
import {storeToRefs} from 'pinia';
import {usePersonaFormStore} from '@/store/persona-form-store';
import axios from 'axios';

export default defineComponent({
  name: 'PersonaTabMain',
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

      await this.uploadImage(selectedFile);

      this.personaForm.imagePath = selectedFile.name;
    },
    async uploadImage(file) {
      const formData = new FormData();
      formData.append('file', file);

      try {
        await axios.post('/api/v1/persona/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });
      } catch (error) {
        console.log('Failed to upload image: ', error);
      }
    },
  },
});
</script>

<template>
  <div class="mb-3">
    <!-- Image upload -->
    <div>
      <div v-if="!personaForm.imagePath"
           class="placeholder-image img-thumbnail d-flex justify-content-center align-items-center">
        <i class="bi bi-person"></i>
      </div>
      <img v-else :src="getImageUrl(personaForm.imagePath)" alt="Avatar" class="img-thumbnail thumbnail-image"/>
    </div>
    <input id="personaImage" ref="fileInput" class="form-control" type="file" @change="onFileSelected">
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
