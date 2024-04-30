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
import AssistantProperties from '@/const/assistant.properties';
import {useAppStore} from '@/store/app-store';
import {useAssistants} from '@/composable/use-assistants';

export default defineComponent({
  name: 'PersonaTabModel',
  data() {
    return {};
  },
  computed: {
    assistantProperties() {
      return AssistantProperties;
    },
  },
  setup() {
    const {assistantForm, systems, models} = storeToRefs(usePersonaFormStore());
    const assistants = useAssistants();
    const appStore = useAppStore();

    return {assistantForm, systems, models, appStore, assistants};
  },
  methods: {
    async onModelChange() {
      try {
        this.models = await this.assistants.retrieveModels(
            this.assistantForm.system);
      } catch (error) {
        this.appStore.handleError(error);
      }
    },
  },
  async mounted() {

  },
});
</script>

<template>
  <div class="mb-3 p-3">
    <select id="selectSystem" v-model="assistantForm.system"
            aria-label="System"
            class="form-select my-2" @change="onModelChange">
      <option v-for="system in systems" :key="system.key" :value="system.key">
        {{ system.description }}
      </option>
    </select>

    <select id="selectChatModel" v-model="assistantForm.model"
            aria-label="Chat Model"
            class="form-select my-2">
      <option v-for="model in models" :key="model" :value="model">{{ model }}
      </option>
    </select>

    <label class="form-label my-2" for="rangeTemperature">Temperature</label>

    <div class="container">
      <div class="row">
        <div class="col-10 p-0">
          <input id="rangeTemperature"
                 v-model="assistantForm.properties[assistantProperties.MODEL_TEMPERATURE]"
                 class="form-range" max="1.0"
                 min="0.0" step="0.1" type="range">
        </div>
        <div class="col-2">
          <label>{{ assistantForm.properties[assistantProperties.MODEL_TEMPERATURE] }}</label>
        </div>
      </div>
    </div>

    <label class="form-label my-2" for="rangeTopP">TopP</label>

    <div class="container">
      <div class="row">
        <div class="col-10 p-0">
          <input id="rangeTopP"
                 v-model="assistantForm.properties[assistantProperties.MODEL_TOP_P]"
                 class="form-range"
                 max="1.0" min="0.0"
                 step="0.1" type="range">
        </div>
        <div class="col-2">
          <label>{{ assistantForm.properties[assistantProperties.MODEL_TOP_P] }}</label>
        </div>
      </div>
    </div>

    <label class="form-label my-2" for="rangeFrequencePenalty">Frequence Penalty</label>

    <div class="container">
      <div class="row">
        <div class="col-10 p-0">
          <input id="rangeFrequencePenalty"
                 v-model="assistantForm.properties[assistantProperties.MODEL_FREQUENCY_PENALTY]"
                 class="form-range"
                 max="1.0" min="0.0"
                 step="0.1" type="range">
        </div>
        <div class="col-2">
          <label>{{
              assistantForm.properties[assistantProperties.MODEL_FREQUENCY_PENALTY]
            }}</label>
        </div>
      </div>
    </div>

    <label class="form-label my-2" for="rangePresencePenalty">Presence Penalty</label>

    <div class="container">
      <div class="row">
        <div class="col-10 p-0">
          <input id="rangePresencePenalty"
                 v-model="assistantForm.properties[assistantProperties.MODEL_PRESENCE_PENALTY]"
                 class="form-range"
                 max="1.0" min="0.0"
                 step="0.1" type="range">
        </div>
        <div class="col-2">
          <label>{{
              assistantForm.properties[assistantProperties.MODEL_PRESENCE_PENALTY]
            }}</label>
        </div>
      </div>
    </div>

  </div>

</template>

<style scoped>
</style>
