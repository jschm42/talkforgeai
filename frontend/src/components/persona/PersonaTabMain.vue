<script lang="ts">
import {defineComponent} from 'vue';

export default defineComponent({
  name: 'PersonaTabMain',
  data() {
    return {
      personaName: '',
      personaSystem: '',
    };
  },
});
</script>

<template>
  <div class="mb-3">
    <label class="form-label" for="personaName">Name</label>
    <input id="personaName" v-model="personaName" class="form-control" required type="text">
  </div>
  <div class="mb-3">
    <label class="form-label" for="personaSystem">System</label>
    <textarea id="personaSystem" v-model="personaSystem" class="form-control" required></textarea>
  </div>
</template>

<style scoped>

</style>
