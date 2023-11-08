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
  <div class="container">
    <div class="row text-start">
      <div class="col-3">
        <img v-if="isShowAssistantImage(selectedAssistant)" :alt="selectedAssistant.name"
             :src="selectedAssistant.imageUrl"
             class="persona-icon"/>
        <i v-else class="fs-1 bi bi-robot robot-icon"></i>
      </div>

      <div class="col-9">
        <div class="row persona-name mx-1">
          {{ personaName }}
        </div>
        <div class="row persona-description mx-1">
          {{ personaDescription }}
        </div>
      </div>
    </div>
    <div class="row">
      <persona-compact-info :assistant="store.selectedAssistant" :show-properties="true"></persona-compact-info>
    </div>
  </div>

</template>

<script>

import {useChatStore} from '@/store/chat-store';
import PersonaCompactInfo from '@/components/persona/PersonaCompactInfo.vue';

export default {
  name: 'ChatHeader',
  components: {PersonaCompactInfo},
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function

    return {store};
  },
  data() {
    return {};
  },
  computed: {
    personaName() {
      return `${this.store.selectedAssistant.name}`;
    },
    personaDescription() {
      return `${this.store.selectedAssistant.description}`;
    },
    isDisabled() {
      return !this.store.chat.configHeaderEnabled;
    },
    personaList() {
      return this.store.personaList;
    },
    selectedAssistant() {
      return this.store.selectedAssistant;
    },
  },
  methods: {
    isShowAssistantImage(assistant) {
      return !!assistant.imagePath;
    },
  },
};
</script>

<style scoped>
.robot-icon {
  margin-right: 8px;
  color: darksalmon;
}

.persona-name {
  font-size: 1.5rem;
  font-weight: 500;
  color: white;
}

.persona-description {
  font-size: 0.9em;
  font-style: italic;
  color: #cccccc;
  overflow: hidden;
}

.back-button {
  font-size: 2.5rem;
  color: #6c757d;
}

.persona-icon {
  width: 7rem;
  height: 7rem
}
</style>
