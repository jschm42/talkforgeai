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

import {useChatStore} from '@/store/chat-store';

export default {
  name: 'AssistantElement',
  components: {},
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    return {store};
  },
  data() {
    return {
      showTooltip: false,
    };
  },
  props: {
    assistant: Object,
  },
  computed: {
    imageSrc() {
      return this.store.getAssistantImageUrl(this.assistant.image_path);
    },
    isShowAssistantImage() {
      return !!this.assistant.image_path;
    },
    hasDescription() {
      return !!this.assistant.description;
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
    onMouseOver() {
      console.log('onMouseOver');
      this.showTooltip = true;
    },
    onMouseLeave() {
      console.log('onMouseLeave');
      this.showTooltip = false;
    },
  },
};
</script>

<template>
  <v-card class="mx-auto" height="280" variant="elevated" width="200">
    <div role="button" @click="onPersonaSelected">
      <v-avatar :image="imageSrc" size="120">
      </v-avatar>
      <v-card-title>{{ assistant.name }}</v-card-title>
      <v-card-text class="description">
        {{ assistant.description }}
      </v-card-text>
    </div>

    <v-card-actions>
      <v-btn color="orange" @click="onEditPersona()">
        Edit
      </v-btn>
    </v-card-actions>

  </v-card>

</template>

<style scoped>

.description {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  word-wrap: break-word;
  margin: 0;
  height: 3.75rem;
}

</style>
