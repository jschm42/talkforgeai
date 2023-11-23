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

<script>

import {useChatStore} from '@/store/chat-store';
import Assistant from '@/store/to/assistant';

export default {
  name: 'AssistantElement',
  components: {},
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    return {store};
  },
  data() {
    return {};
  },
  props: {
    assistant: Assistant,
  },
  computed: {
    imageSrc() {
      return this.store.getAssistantImageUrl(this.assistant.image_path);
    },
    isShowAssistantImage() {
      return !!this.assistant.image_path;
    },
  },
  methods: {
    onPersonaSelected() {
      console.log('PersonaChoiceView.onPersonaSelected: ' + this.assistant.id,
          this.store.assistantList);
      this.$router.push({name: 'chat', params: {assistantId: this.assistant.id}});
    },
    onEditPersona() {
      this.$router.push({name: 'persona-edit', params: {assistantId: this.assistant.id}});
    },
  },
};
</script>

<template>
  <div class="card persona-card">
    <div class="card-image">
      <img v-if="isShowAssistantImage" :alt="assistant.name" :src="imageSrc"
           class="persona-icon" role="button" @click.prevent="onPersonaSelected()"/>
      <i v-else class="fs-1 bi bi-robot robot-icon"></i>
      <h5 class="card-title" @click.prevent="onPersonaSelected()">{{ assistant.name }}</h5>
      <div class="card-description">{{ assistant.description }}
      </div>
    </div>
    <div class="col-2 d-inline-flex flex-row-reverse my-1 p-2">
      <i class="bi bi-pencil edit-button" role="button"
         @click.prevent="onEditPersona()"></i>
    </div>
  </div>
</template>

<style scoped>
.persona-card {
  min-width: 150px;
  min-height: 150px;
  width: 150px; /* Set a fixed width */
  height: 150px; /* Set a fixed height */
  position: relative;
}

.persona-icon {
  width: 100%;
  height: auto;
}

.robot-icon {
  width: 100%;
  height: 100px;
}

.card-image {
  width: 100%;
  height: 100%;
  position: relative;
}

.card-title {
  position: absolute;
  top: 10px;
  left: 10px;
  color: white;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  background-color: rgba(0, 0, 0, 0.5);
  padding: 2px;
}

.card-description {
  position: absolute;
  bottom: 0px;
  left: 10px;
  color: white;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  background-color: rgba(0, 0, 0, 0.5);
  padding: 2px;
  font-style: italic;
  display: -webkit-box; /* Required for line-clamp to work */
  -webkit-line-clamp: 2; /* Truncate text at 2 lines */
  -webkit-box-orient: vertical; /* Required for line-clamp to work */
  overflow: hidden; /* Hides the text that overflows the card width */
  text-overflow: ellipsis; /* Shows an ellipsis when the text overflows */
  width: 90%; /* Adjust as needed */
}

.edit-button {
  position: absolute;
  top: 10px;
  right: 10px;
  color: white;
  background-color: rgba(0, 0, 0, 0.7); /* Add a semi-transparent white background */
  border-radius: 50%; /* Make the button round */
  padding: 5px; /* Add some padding */
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}
</style>
