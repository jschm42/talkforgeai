<script>
import {defineComponent} from 'vue';
import {storeToRefs} from 'pinia';
import {usePersonaFormStore} from '@/store/persona-form-store';
import PersonaProperties from '@/service/persona.properties';

export default defineComponent({
  name: 'PersonaTabFeatures',
  computed: {
    PersonaProperties() {
      return PersonaProperties;
    },
  },
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
      <input id="checkboxTitleGeneration" v-model="personaForm.properties[PersonaProperties.FEATURE_TITLEGENERATION]"
             class="form-check-input" type="checkbox">
      <label class="form-check-label" for="checkboxTitleGeneration">
        Title generation
      </label>
    </div>
    <div class="form-check">
      <input id="checkboxImageGeneration" v-model="personaForm.properties[PersonaProperties.FEATURE_IMAGEGENERATION]"
             class="form-check-input" type="checkbox">
      <label class="form-check-label" for="checkboxImageGeneration">
        Image generation
      </label>
    </div>
    <div class="form-check">
      <input id="checkboxPlantUML" v-model="personaForm.properties[PersonaProperties.FEATURE_PLANTUML]"
             class="form-check-input" type="checkbox">
      <label class="form-check-label" for="checkboxPlantUML">
        PlantUML generation
      </label>
    </div>
    <div class="form-check">
      <input id="checkBoxAutoSpeakDefault" v-model="personaForm.properties[PersonaProperties.FEATURE_AUTOSPEAKDEFAULT]"
             class="form-check-input" type="checkbox">
      <label class="form-check-label" for="checkBoxAutoSpeakDefault">
        Auto Speak is turned on by default
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
