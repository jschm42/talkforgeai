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

<template>
  <v-img v-if="isShowAssistantImage(selectedAssistant)"
         :src="imageSrc(selectedAssistant.image_path)"
         alt="Persona Image"
         class="mx-auto"
         max-height="200"
         max-width="500"
  >
  </v-img>
  <img v-else alt="Robot" class="robot-icon" src="@/assets/robot.svg">
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

.robot-icon {
  margin: 2rem;
}

</style>
