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

  <v-app>
    <v-navigation-drawer absolute permanent width="200">
      <v-list-item>
        <img alt="Talkforge AI" class="logo" role="button"
             src="@/assets/logo.png" style="width: 90%" title="Talkforge AI"
             @click.prevent="onListAssistants">

        <div class="d-flex flex-grow-1 align-items-start">
        </div>
      </v-list-item>
      <v-list density="comfortable" lines="false" nav>
        <v-list-subheader>Main</v-list-subheader>
        <v-list-item color="primary" title="Assistants" variant="elevated"
                     @click.prevent="onListAssistants">
          <template v-slot:prepend>
            <v-icon icon="mdi-head-snowflake"></v-icon>
          </template>
        </v-list-item>
        <v-list-item color="primary" title="Create assistant" variant="elevated"
                     @click.prevent="onCreateNewAssistant">
          <template v-slot:prepend>
            <v-icon icon="mdi-plus-thick"></v-icon>
          </template>
        </v-list-item>
        <v-list-item color="primary" title="Memory" variant="elevated"
                     @click.prevent="onOpenMemoryEditor">
          <template v-slot:prepend>
            <v-icon icon="mdi-memory"></v-icon>
          </template>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>

    <v-main>
      <router-view name="mainView"/>
    </v-main>
  </v-app>


  <error-alerts></error-alerts>
</template>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  height: 100%;
}

.text-gradient-silver {
  background-image: linear-gradient(to top, #555555 0%, #ffffff 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  display: inline-block;
  -webkit-text-fill-color: transparent; /* For Safari compatibility */
}

h1 {
  font-size: 1.5rem;
}

h2 {
  font-size: 1.25rem;
}

</style>


<script>
import ErrorAlerts from '@/components/common/ErrorAlerts.vue';
import {onMounted} from 'vue';
import {useRouter} from 'vue-router';
import {useAppStore} from '@/store/app-store';
import {useAssistants} from '@/composable/use-assistants';
import {useChatStore} from '@/store/chat-store';

export default {
  name: 'ChatContainer',
  components: {ErrorAlerts},
  setup() {
    const appStore = useAppStore();
    const chatStore = useChatStore();
    const assistants = useAssistants();
    const router = useRouter();

    const onListAssistants = () => {
      router.push({name: 'assistant-choice'});
    };

    const onCreateNewAssistant = () => {
      router.push({name: 'assistant-create'});
    };

    const onOpenMemoryEditor = () => {
      router.push({name: 'memory-editor'});
    };

    onMounted(async () => {
      try {
        chatStore.assistantList = await assistants.retrieveAssistants();
      } catch (error) {
        appStore.handleError(error);
      }
    });

    return {
      onListAssistants,
      onCreateNewAssistant,
      onOpenMemoryEditor,
    };
  },
};
</script>
