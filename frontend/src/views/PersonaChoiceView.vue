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
  <div class="container p-3">
    <div class="row header">
      <div class="header">
        <img class="logo" src="@/assets/logo.png" title="Talkforge AI">
      </div>
    </div>
    <div class="row scrollable-persona-list">

      <div v-for="assistant in assistantList" :key="assistant.id" class="col-md-3">
        <div class="card mb-3 persona-card"
             style="max-width: 300px;">
          <div class="row g-0">
            <div class="col-md-4">
              <img v-if="isShowAssistantImage(assistant)" :alt="assistant.name" :src="imageSrc(assistant.image_path)"
                   class="persona-icon" role="button" @click.prevent="onPersonaSelected(assistant.id)"/>
              <i v-else class="fs-1 bi bi-robot robot-icon"></i>
            </div>
            <div class="col-md-6">
              <div class="card-body">
                <h5 class="card-title" @click.prevent="onPersonaSelected(assistant)">{{ assistant.name }}</h5>
              </div>
            </div>

            <div class="col-2 d-inline-flex flex-row-reverse my-1 p-2">
              <i class="bi bi-pencil edit-button" role="button" @click.prevent="onEditPersona(assistant.id)"></i>
            </div>
          </div>

          <div class="row g-0 p-3" role="button" @click.prevent="onPersonaSelected(assistant.id)">
            <small class="text-body-secondary">{{ assistant.description }}</small>
          </div>

          <persona-compact-info :assistant="assistant"></persona-compact-info>

        </div>

      </div>

      <div class="col-md-3">
        <div class="card bg-gradient text-center persona-card" role="button" @click.prevent="onCreateNewPersona">
          <div class="card-body">
            <h5 class="card-title">
              Create new assistant...
            </h5>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue';
import {useChatStore} from '@/store/chat-store';
import PersonaCompactInfo from '@/components/persona/PersonaCompactInfo.vue';

export default defineComponent({
  components: {PersonaCompactInfo},
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    return {store};
  },
  data() {
    return {
      isEntrySelected: false,
    };
  },
  computed: {
    assistantList() {
      return this.store.assistantList;
    },
  },
  methods: {
    imageSrc(imagePath) {
      return this.store.getAssistantImageUrl(imagePath);
    },
    isShowAssistantImage(assistant) {
      return !!assistant.image_path;
    },
    onPersonaSelected(assistantId) {
      console.log('PersonaChoiceView.onPersonaSelected: ' + assistantId, this.store.assistantList);
      this.$router.push({name: 'chat', params: {assistantId}});
    },
    onCreateNewPersona() {
      this.$router.push({name: 'persona-create'});
    },
    onEditPersona(assistantId) {
      this.$router.push({name: 'persona-edit', params: {assistantId}});
    },
  },
  async mounted() {
    await this.store.syncAssistants();
  },
});
</script>

<style scoped>
.robot-icon {
  color: darksalmon;
}

.persona-card {
  min-width: 200px;
  min-height: 170px;
}

.persona-icon {
  width: 6em;
  height: 6em;
}

.active {
  border: 2px solid #007BFF; /* blue border */
  background: #2c3e50; /* grey-ish background */
}

.scrollable-persona-list {
  height: 75vh;
  overflow-y: auto;
}

.logo {
  height: 10em;
  margin-left: 8px;
}

.header {
  margin-bottom: 1em;
}

.edit-button {
  font-size: 1.3em;
}

</style>
