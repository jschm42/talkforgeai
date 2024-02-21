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
  <div class="card assistant-element">
    <img v-if="isShowAssistantImage" :alt="assistant.name" :src="imageSrc" :title="assistant.name"
         class="persona-icon card-img-top img-fluid p-2" role="button"
         @click.prevent="onPersonaSelected()"/>
    <img v-else :alt="assistant.name" class="robot-icon card-img-top img-fluid p-2"
         src="@/assets/robot.svg"
         title="Robot"
         @click.prevent="onPersonaSelected()">
    <i class="bi bi-pencil edit-button rounded-5" role="button"
       @click.prevent="onEditPersona()"></i>
    <div class="card-body" role="button" @click.prevent="onPersonaSelected()">
      <h5 :title="assistant.name" class="card-title title">{{ assistant.name }}</h5>
      <div :title="assistant.description" class="card-text description truncate-text">
        {{ assistant.description }}
      </div>
    </div>
  </div>

</template>

<style scoped>

.assistant-element {
  background-color: #303030;
}


.robot-icon {
  color: #cccccc;
}

.edit-button {
  position: absolute;
  top: 0.1rem;
  right: 0.1rem;
  padding: 0.5rem;
  color: #cccccc;
  font-size: 1.3rem;
  background-color: rgba(20, 20, 20, 0.7);
}

.truncate-text {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.persona-icon {
  height: auto;
}

.description {
  font-size: 0.8rem;
  color: #cccccc;
}


@media only screen and (min-width: 768px ) {
  .description {
    font-size: 1rem;
  }
}

.title {
  font-size: 1rem;
  font-weight: bold;
  color: #cccccc;
}

@media only screen and (min-width: 768px ) {
  .title {
    font-size: 1.1rem;
  }
}

</style>
