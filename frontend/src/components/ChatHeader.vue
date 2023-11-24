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
    <div class="row">
      <div class="col-12">
        <div class="persona-info">
          <figure class="figure persona-icon">
            <img v-if="isShowAssistantImage(selectedAssistant)"
                 :src="imageSrc(selectedAssistant.image_path)" alt="..."
                 class="figure-img img-fluid rounded">
            <img v-else src="@/assets/robot.svg" title="Robot">
            <figcaption class="figure-caption">{{ personaName }}</figcaption>
          </figure>
          <div class="persona-description">
            {{ personaDescription }}
          </div>
        </div>
      </div>
    </div>
  </div>

  <!--        <img v-if="isShowAssistantImage(selectedAssistant)" :alt="selectedAssistant.name"-->
  <!--             :src="imageSrc(selectedAssistant.image_path)"-->
  <!--             class="persona-icon"/>-->
  <!--        <img v-else class="robot-icon" src="@/assets/robot.svg" title="Robot">-->


  <!--        <div class="row persona-name mx-1">-->
  <!--          {{ personaName }}-->
  <!--        </div>-->
  <!--        <div class="row persona-description mx-1">-->
  <!--          {{ personaDescription }}-->
  <!--        </div>-->


</template>

<script>

import {useChatStore} from '@/store/chat-store';

export default {
  name: 'ChatHeader',
  components: {},
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
  display: flex;
  align-items: flex-start;
}

.persona-description {
  margin-left: 10px;
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
  height: 100px;
}

.back-button {
  font-size: 2.5rem;
  color: #6c757d;
}

.persona-icon {
  min-width: 150px;
  min-height: 150px;
  max-width: 200px;
  max-height: 200px;
  width: 150px;
  height: 1500px;

}
</style>
