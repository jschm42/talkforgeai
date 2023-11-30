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
  <div class="p-2">
    <div class="row">

    </div>

    <div class="row g-1 full-height">
      <!-- History Column -->
      <div class="col-3">
        <ChatHeader></ChatHeader>
        <ThreadList></ThreadList>
      </div>
      <!-- End History Column -->

      <div class="col-9">
        <ChatContainer></ChatContainer>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue';
import {useChatStore} from '@/store/chat-store';
import {useAppStore} from '@/store/app-store';
import ChatHeader from '@/components/chat/ChatHeader.vue';
import ChatContainer from '@/components/chat/ChatContainer.vue';
import ThreadList from '@/components/thread/ThreadList.vue';

export default defineComponent({
  components: {ThreadList, ChatHeader, ChatContainer},
  props: ['assistantId'],
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    const appStore = useAppStore();
    return {store, appStore};
  },
  data() {
    return {};
  },
  methods: {
    async fetchData() {
      try {
        await this.store.selectAssistant(this.assistantId);
        await this.store.retrieveThreads();
      } catch (error) {
        this.appStore.handleError(error);
      }
    },
  },
  mounted() {
    this.fetchData();
  },
});
</script>

<style>
pre {
  background-color: #303030;
  color: white;
  border: 1px solid #e9ecef;
  border-radius: 0.25rem;
  padding: 1rem;
}

.code-word {
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 0.25rem;
  /*font-family: "Source Code Pro", monospace;*/
  font-family: 'Courier New', monospace
}

body, html {
  overflow: hidden;
  height: 100vh;
}

.full-height {
  height: 100vh;
}

.vertical-scrollbar {
  overflow-y: auto;
}

.no-horizontal-scrollbar {
  overflow-x: hidden;
}

.image-prompt-element {
  background-color: bisque;
  border-color: black;
  border-radius: 5px;
  box-shadow: 3px 3px 2px 1px rgba(50, 50, 50, .2);
  min-width: 110px;
  min-height: 23px;
  padding: 3px;
}

</style>

