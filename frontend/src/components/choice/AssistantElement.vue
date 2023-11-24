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
import AssistantTooltip from '@/components/AssistantTooltip.vue';

export default {
  name: 'AssistantElement',
  components: {AssistantTooltip},
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
    assistant: Assistant,
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
  mounted() {
    console.log('AssistantElement.mounted: ', this.assistant);
  },
};
</script>

<template>
  <div class="persona-card">

    <div class="card-image" role="button" @mouseleave="onMouseLeave()" @mouseover="onMouseOver()">
      <img v-if="isShowAssistantImage" :alt="assistant.name" :src="imageSrc"
           class="persona-icon rounded-border" role="button"
           @click.prevent="onPersonaSelected()"/>
      <img v-else class="robot-icon rounded-border" src="@/assets/robot.svg" title="Robot"
           @click.prevent="onPersonaSelected()">
      <h5 class="card-title" @click.prevent="onPersonaSelected()">{{ assistant.name }}</h5>
      <assistant-tooltip v-if="showTooltip && hasDescription" @click.prevent="onPersonaSelected()">
        {{ assistant.description }}
      </assistant-tooltip>
    </div>
    <div class="col-2 d-inline-flex flex-row-reverse my-1 p-2">
      <i class="bi bi-pencil edit-button" role="button"
         @click.prevent="onEditPersona()"></i>
    </div>

  </div>

</template>

<style scoped>
.persona-card {
  min-width: 200px;
  min-height: 170px;
  width: 200px; /* Set a fixed width */
  height: 170px; /* Set a fixed height */
  position: relative;
  display: inline-block;
  border-radius: 10px;
}

.persona-icon {
  width: 100%;
  height: auto;
}

.robot-icon {
  width: 100%;
  height: auto;
  padding: 50px;
  color: #cccccc;
  background-color: #303030;
}

.card-image {
  width: 100%;
  height: 100%;
  position: relative;
  display: inline-block;
}

.card-title {
  position: absolute;
  top: 10px;
  left: 10px;
  color: white;
  background-color: rgba(0, 0, 0, 0.5);
  padding: 2px;
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

.rounded-border {
  border-radius: 15px;
}

</style>
