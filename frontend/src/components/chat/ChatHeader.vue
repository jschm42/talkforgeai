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
         :src="imageSrc(selectedAssistant.imagePath)"
         alt="Persona Image"
         class="mx-auto"
         max-height="200"
         max-width="500"
  >
  </v-img>
  <v-img v-else alt="Robot" class="mx-auto" max-height="200"
         max-width="500" src="@/assets/robot.svg"></v-img>

</template>

<script>

import {useChatStore} from '@/store/chat-store';
import {useAssistants} from '@/composable/use-assistants';

export default {
  name: 'ChatHeader',
  components: {},
  props: {
    showName: Boolean,
  },
  setup() {
    const chatStore = useChatStore(); // Call useMyStore() inside the setup function
    const assistants = useAssistants();

    return {chatStore, assistants};
  },
  data() {
    return {};
  },
  computed: {
    personaName() {
      return `${this.chatStore.selectedAssistant.name}`;
    },
    personaDescription() {
      return `${this.chatStore.selectedAssistant.description}`;
    },
    personaList() {
      return this.chatStore.personaList;
    },
    selectedAssistant() {
      return this.chatStore.selectedAssistant;
    },
  },
  methods: {
    imageSrc(imagePath) {
      return this.assistants.getAssistantImageUrl(imagePath);
    },
    isShowAssistantImage(assistant) {
      return !!assistant.imagePath;
    },
  },
};
</script>
