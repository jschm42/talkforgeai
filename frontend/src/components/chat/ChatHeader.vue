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
  <div class="persona-info">
    <figure v-if="showName" class="figure persona-icon">
      <img v-if="isShowAssistantImage(selectedAssistant)"
           :src="imageSrc(selectedAssistant.image_path)" alt="Persona Image"
           class="figure-img img-fluid rounded">
      <img v-else alt="Robot Image" class="robot-icon"
           src="@/assets/robot.svg" title="Robot">
      <figcaption class="figure-caption">{{ personaName }}</figcaption>
    </figure>
    <div v-else>
      <img v-if="isShowAssistantImage(selectedAssistant)"
           :src="imageSrc(selectedAssistant.image_path)" alt="Persona Image"
           class="img-fluid rounded persona-icon">
      <img v-else alt="Robot Image" class="robot-icon" src="@/assets/robot.svg" title="Robot">
    </div>
  </div>

</template>

<script>

import {useChatStore} from '@/store/chat-store';

export default {
  name: 'ChatHeader',
  components: {},
  props: {
    showName: Boolean,
  },
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
    imageSrc(imagePath) {
      return this.store.getAssistantImageUrl(imagePath);
    },
    isShowAssistantImage(assistant) {
      return !!assistant.image_path;
    },
  },
};
</script>

<style scoped>

.persona-info {
  max-width: 15rem;
  margin-left: auto;
  margin-right: auto;
}

.robot-icon {
  margin: 2rem;
}

</style>
