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
  <v-app-bar>
    <v-toolbar dark>
      <img alt="Talkforge AI" class="logo-small" src="@/assets/logo-notext.png">
      <v-toolbar-title>Talkforge AI</v-toolbar-title>
      <v-menu>
        <template v-slot:activator="{ props }">
          <v-btn icon="mdi-dots-vertical" v-bind="props"></v-btn>
        </template>

        <v-list>
          <v-list-item @click="onCreateNewPersona">
            <v-list-item-title>Create persona</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-toolbar>
  </v-app-bar>

  <v-main>
    <!-- Main Content -->
    <div class="col-12 col-lg-10">

      <!-- Your main content goes here -->
      <div class="row">

        <div class="container" style="overflow: auto; height: 90vh">
          <div class="d-flex flex-wrap flex-row">
            <div v-for="assistant in assistantList"
                 :key="assistant.id" class="d-flex flex-column m-1 assistant-element">
              <assistant-element :assistant="assistant"></assistant-element>
            </div>
          </div>
        </div>
      </div>


    </div>

  </v-main>

</template>

<script>
import {defineComponent} from 'vue';
import {useChatStore} from '@/store/chat-store';
import {useAppStore} from '@/store/app-store';
import AssistantElement from '@/components/assistant/AssistantElement.vue';
import {useAssistants} from '@/composable/use-assistants';

export default defineComponent({
  components: {AssistantElement},
  setup() {
    const chatStore = useChatStore(); // Call useMyStore() inside the setup function
    const appStore = useAppStore();
    const assistants = useAssistants();
    return {chatStore, appStore, assistants};
  },
  data() {
    return {
      showSidebar: false,
      isEntrySelected: false,
    };
  },
  computed: {
    assistantList() {
      return this.chatStore.assistantList;
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
});
</script>

<style scoped>
.logo {
  height: 10rem;
  margin: 1rem auto;
}

h1 {
  color: #cccccc;
}

.logo-small {
  height: 3rem;
}

hr {
  color: #ffffff;
}

ul {
  font-size: 1.5rem;
}

.assistant-element {
  width: 8rem;
  height: 16rem;
}

@media only screen and (min-width: 768px ) {
  .assistant-element {
    width: 15rem;
    height: 21rem;
  }
}

@media only screen and (min-width: 375px) {
  .assistant-element {
    width: 11rem;
    height: 19rem;
  }
}

</style>
