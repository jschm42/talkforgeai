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

  <!-- Main Content -->

  <div class="container" style="overflow: auto; height: 90vh">
    <div class="d-flex flex-wrap flex-row">
      <div v-for="assistant in assistantList"
           :key="assistant.id" class="d-flex flex-column m-1">
        <assistant-element :assistant="assistant"></assistant-element>
      </div>
    </div>
  </div>

</template>

<script>
import {computed, onMounted} from 'vue';
import {useAppStore} from '@/store/app-store';
import {useAssistants} from '@/composable/use-assistants';
import AssistantElement from '@/components/assistant/AssistantElement.vue';
import {useChatStore} from '@/store/chat-store';

export default {
  components: {AssistantElement},
  setup() {
    const chatStore = useChatStore();
    const appStore = useAppStore();
    const assistants = useAssistants();

    const assistantList = computed(() => {
      return chatStore.assistantList;
    });

    onMounted(async () => {
      try {
        chatStore.assistantList = await assistants.retrieveAssistants();
      } catch (error) {
        appStore.handleError(error);
      }
    });

    return {
      assistantList,
      chatStore,
    };
  },
};

</script>

<style scoped>

</style>
