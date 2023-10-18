<script>
import {defineComponent} from 'vue';
import {storeToRefs} from 'pinia';
import {usePersonaFormStore} from '@/store/persona-form-store';

export default defineComponent({
  name: 'PersonaTabFeatures',
  data() {
    return {};
  },
  setup() {
    const {personaForm} = storeToRefs(usePersonaFormStore());

    return {personaForm};
  },
  methods: {},
  mounted() {
  },
});
</script>

<template>
  <div class="mb-3 p-3">
    <div class="form-check">
      <input id="checkboxImageGeneration" v-model="personaForm.properties.feature_imageGeneration"
             class="form-check-input" type="checkbox">
      <label class="form-check-label" for="checkboxImageGeneration">
        Image generation
      </label>
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

</style>
