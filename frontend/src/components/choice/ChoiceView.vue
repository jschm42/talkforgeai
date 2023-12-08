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

    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <a class="navbar-brand" href="#">
        <img alt="Talkforge AI" class="logo" src="@/assets/logo.png" title="Talkforge AI">
      </a>
      <button aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation"
              class="navbar-toggler"
              data-target="#navbarNav" data-toggle="collapse" type="button">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div id="navbarNav" class="collapse navbar-collapse">
        <ul class="navbar-nav">
          <li class="nav-item active fs-2">
            <a class="nav-link" href="#" @click.prevent="onCreateNewPersona">Create assistant...</a>
          </li>
        </ul>
      </div>
    </nav>

    <div class="row">

      <div class="d-flex flex-wrap justify-content-start scrollable-persona-list">
        <div v-for="assistant in assistantList" :key="assistant.id"
             class="p-1 m-1 assistant-element">
          <assistant-element :assistant="assistant"></assistant-element>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue';
import {useChatStore} from '@/store/chat-store';
import {useAppStore} from '@/store/app-store';
import AssistantElement from '@/components/assistant/AssistantElement.vue';

export default defineComponent({
  components: {AssistantElement},
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    const appStore = useAppStore();
    return {store, appStore};
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
    try {
      await this.store.syncAssistants();
    } catch (error) {
      this.appStore.handleError(error);
    }
  },
});
</script>

<style scoped>
.scrollable-persona-list {
  height: 75vh;
  overflow-y: auto;
}

.assistant-element {
  width: 13rem;
  height: 21rem;
}

.logo {
  height: 7rem;
  margin-left: 8px;
}

</style>
