<script lang="ts">
import {defineComponent} from 'vue';

export default defineComponent({
  name: 'PersonaTabMain',
  data() {
    return {
      personaName: '',
      personaDescription: '',
      personaSystem: '',
    };
  },
});
</script>

<template>
  <div class="mb-3">
    <i class="fs-1 bi bi-person"></i>
  </div>
  <div class="mb-3">
    <label class="form-label" for="personaName">Name</label>
    <input id="personaName" v-model="personaName" class="form-control" maxlength="32" required type="text">
  </div>
  <div class="mb-3">
    <label class="form-label" for="personaDescription">Description</label>
    <textarea id="personaDescription" v-model="personaDescription" class="form-control" maxlength="256"
              rows="4"></textarea>
  </div>
  <div class="mb-3">
    <label class="form-label" for="personaSystem">System</label>
    <textarea id="personaSystem" v-model="personaSystem" class="form-control" maxlength="16384" rows="10"></textarea>
  </div>
</template>

<style scoped>

</style>
