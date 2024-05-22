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
import {useAssistantFormStore} from '@/store/persona-form-store';
import AssistantProperties from '@/const/assistant.properties';

export default defineComponent({
  name: 'PersonaTabFeatures',
  computed: {
    AssistantProperties() {
      return AssistantProperties;
    },
  },
  data() {
    return {};
  },
  setup() {
    const {assistantForm} = storeToRefs(useAssistantFormStore());

    return {assistantForm};
  },
  methods: {},
  mounted() {
  },
});
</script>

<template>
  <div class="mb-3 p-3">
    <div class="form-check">
      <input id="checkboxTitleGeneration"
             v-model="assistantForm.properties[AssistantProperties.FEATURE_TITLEGENERATION]"
             class="form-check-input" type="checkbox">
      <label class="form-check-label" for="checkboxTitleGeneration">
        Title generation (uses OpenAI ChatGPT 3.5)
      </label>
    </div>
    <div class="form-check">
      <input id="checkboxImageGeneration"
             v-model="assistantForm.properties[AssistantProperties.FEATURE_IMAGEGENERATION]"
             class="form-check-input" type="checkbox">
      <label class="form-check-label" for="checkboxImageGeneration">
        Image generation
      </label>
    </div>
    <div class="form-check">
      <input id="checkboxPlantUML"
             v-model="assistantForm.properties[AssistantProperties.FEATURE_PLANTUML]"
             class="form-check-input" type="checkbox">
      <label class="form-check-label" for="checkboxPlantUML">
        PlantUML generation
      </label>
    </div>
    <div class="form-check">
      <input id="checkBoxAutoSpeakDefault"
             v-model="assistantForm.properties[AssistantProperties.FEATURE_AUTOSPEAKDEFAULT]"
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
