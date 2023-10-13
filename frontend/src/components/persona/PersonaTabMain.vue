<script>
import {defineComponent} from 'vue';
import {storeToRefs} from 'pinia';
import {usePersonaFormStore} from '@/store/persona-form-store';

export default defineComponent({
  name: 'PersonaTabMain',
  setup() {
    const {personaForm} = storeToRefs(usePersonaFormStore());

    return {personaForm};
  },
});
</script>

<template>
  <div class="mb-3">
    <i class="fs-1 bi bi-person"></i>
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

</style>
