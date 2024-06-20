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
  name: 'PersonaTabMemory',
  data() {
    return {
      memoryTypes: [
        {key: 'NONE', description: 'No information is stored'},
        {
          key: 'AI_DECIDES',
          description: 'The AI independently decides which information is stored',
        },
        {key: 'HISTORY', description: 'The entire chat history is stored'},
      ],
    };
  },
  computed: {
    assistantProperties() {
      return AssistantProperties;
    },
  },
  setup() {
    const {assistantForm} = storeToRefs(useAssistantFormStore());
    return {assistantForm};
  },
  methods: {},
});
</script>

<template>
  <div class="mb-3 p-3 col-4">
    <select id="selectMemory" v-model="assistantForm.memory"
            aria-label="Memory Type"
            class="form-select my-2" @change="onModelChange">
      <option v-for="memoryType in memoryTypes" :key="memoryType.key"
              :value="memoryType.key">
        {{ memoryType.description }}
      </option>
    </select>
  </div>

</template>

<style scoped>
</style>
