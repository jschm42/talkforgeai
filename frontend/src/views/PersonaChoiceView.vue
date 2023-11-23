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
        <assistant-element :assistant="assistant"></assistant-element>
      </div>

      <div class="col-md-3">
        <div class="card bg-gradient text-center persona-card" role="button"
             @click.prevent="onCreateNewPersona">
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
import AssistantElement from '@/components/choice/AssistantElement.vue';

export default defineComponent({
  components: {AssistantElement},
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

    isShowAssistantImage(assistant) {
      return !!assistant.image_path;
    },

    onCreateNewPersona() {
      this.$router.push({name: 'persona-create'});
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
