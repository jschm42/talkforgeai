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
  <div class="persona-card">
    <div class="card-image">
      <img v-if="isShowAssistantImage" :alt="assistant.name" :src="imageSrc"
           class="persona-icon" role="button" @click.prevent="onPersonaSelected()"/>

      <svg v-else class="bi bi-robot robot-icon" fill="currentColor" viewBox="0 0 16 16"
           xmlns="http://www.w3.org/2000/svg">
        <path
            d="M6 12.5a.5.5 0 0 1 .5-.5h3a.5.5 0 0 1 0 1h-3a.5.5 0 0 1-.5-.5M3 8.062C3 6.76 4.235 5.765 5.53 5.886a26.58 26.58 0 0 0 4.94 0C11.765 5.765 13 6.76 13 8.062v1.157a.933.933 0 0 1-.765.935c-.845.147-2.34.346-4.235.346-1.895 0-3.39-.2-4.235-.346A.933.933 0 0 1 3 9.219zm4.542-.827a.25.25 0 0 0-.217.068l-.92.9a24.767 24.767 0 0 1-1.871-.183.25.25 0 0 0-.068.495c.55.076 1.232.149 2.02.193a.25.25 0 0 0 .189-.071l.754-.736.847 1.71a.25.25 0 0 0 .404.062l.932-.97a25.286 25.286 0 0 0 1.922-.188.25.25 0 0 0-.068-.495c-.538.074-1.207.145-1.98.189a.25.25 0 0 0-.166.076l-.754.785-.842-1.7a.25.25 0 0 0-.182-.135Z"/>
        <path
            d="M8.5 1.866a1 1 0 1 0-1 0V3h-2A4.5 4.5 0 0 0 1 7.5V8a1 1 0 0 0-1 1v2a1 1 0 0 0 1 1v1a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2v-1a1 1 0 0 0 1-1V9a1 1 0 0 0-1-1v-.5A4.5 4.5 0 0 0 10.5 3h-2zM14 7.5V13a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V7.5A3.5 3.5 0 0 1 5.5 4h5A3.5 3.5 0 0 1 14 7.5"/>
      </svg>
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
  height: auto;
  padding: 50px;
  color: #6c757d;
  background-color: #303030;
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
  background-color: rgba(0, 0, 0, 0.5);
  padding: 2px;
}

.card-description {
  position: absolute;
  bottom: -20px;
  left: 10px;
  color: white;
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
