<!--
  - Copyright (c) 2023 Jean Schmitz.
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

<template>
  <!-- Align the badges horizontally -->

  <div class="container p-0 m-0">
    <div class="d-flex flex-row">
      <div v-for="item in properties" :key="item.key" class="p-1">
        <span :class="badgeColor(item.key)" class="badge rounded-pill">{{ badgeLabel(item) }}</span>
      </div>
    </div>
  </div>
</template>

<script>

import {useChatStore} from '@/store/chat-store';
import Assistant from '@/store/to/assistant';
import AssistantProperties, {TTSType} from '@/service/assistant.properties';

export default {
  name: 'PersonaCompactInfo',
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function

    return {store};
  },
  data() {
    return {
      allowedKeys: [],
    };
  },
  props: {
    assistant: {
      type: Assistant,
      required: true,
    },
    showProperties: {
      type: Boolean,
      default: false,
    },
  },
  mounted() {
    if (this.showProperties) {
      this.allowedKeys.push(
        AssistantProperties.CHATGPT_TEMPERATURE,
        AssistantProperties.CHATGPT_TOP_P,
      );
    }
  },
  computed: {
    properties() {
      let result = [];

      // if (this.assistant.properties[PersonaProperties.CHATGPT_MODEL]) {
      //   result.push({
      //     key: PersonaProperties.CHATGPT_MODEL,
      //     value: this.persona.properties[PersonaProperties.CHATGPT_MODEL],
      //   });
      // }
      //
      // if (this.persona.properties[PersonaProperties.TTS_TYPE] === TTSType.ELEVENLABS) {
      //   result.push({
      //     key: TTSType.ELEVENLABS,
      //     value: this.persona.properties[PersonaProperties.ELEVENLABS_MODELID],
      //   });
      // } else if (this.persona.properties[PersonaProperties.TTS_TYPE] === TTSType.SPEECHAPI) {
      //   result.push({
      //     key: TTSType.SPEECHAPI,
      //     value: this.persona.properties[PersonaProperties.SPEECHAPI_VOICE],
      //   });
      // }

      // if (this.persona.properties) {
      //   Object.keys(this.persona.properties).filter((key) => {
      //     return this.allowedKeys.indexOf(key) !== -1;
      //   }).forEach((key) => {
      //     result.push({key: key, value: this.persona.properties[key]});
      //   });
      // }

      return result;
    },
  },
  methods: {
    badgeColor(key) {
      switch (key) {
        case AssistantProperties.CHATGPT_MODEL:
          return 'text-bg-primary';
        case TTSType.ELEVENLABS:
        case TTSType.SPEECHAPI:
          return 'text-bg-info';
        case AssistantProperties.CHATGPT_TEMPERATURE:
          return 'text-bg-success';
        case AssistantProperties.ELEVENLABS_MODELID:
          return 'text-bg-warning';
        default:
          return 'text-bg-secondary';
      }
    },
    badgeLabel(item) {
      switch (item.key) {
        case AssistantProperties.CHATGPT_MODEL:
        case AssistantProperties.ELEVENLABS_MODELID:
          return item.value;
        case AssistantProperties.CHATGPT_TEMPERATURE:
          return 'Temp: ' + item.value;
        case AssistantProperties.CHATGPT_TOP_P:
          return 'TopP: ' + item.value;
        case TTSType.SPEECHAPI:
          return 'SpeechAPI';
        case TTSType.ELEVENLABS:
          return 'Elevenlabs';
        default:
          return 'Unknown';
      }
    },
  },
};
</script>

<style scoped>

</style>
