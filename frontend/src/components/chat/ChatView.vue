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
  <div class="container-fluid">

    <div class="row">
      <!-- Sidebar visible on lg and higher screens -->
      <div class="col-lg-3 d-none d-lg-block sidebar g-1">
        <!-- Sidebar content -->
        <div class="row my-2 m-auto">
          <ChatHeader></ChatHeader>
        </div>
        <div class="row m-auto">
          <ThreadList></ThreadList>
        </div>

        <!-- Add more buttons as needed -->
      </div>
      <div class="col-12 col-lg-9">
        <!-- Main Content -->
        <div class="row toolbar-header p-2 d-lg-none">
          <div class="d-flex">
            <div style="width: 3rem">
              <ChatHeader :show-name="false"></ChatHeader>
            </div>

            <h1 class="flex-grow-1 d-lg-none">{{ assistantName }}</h1>

            <!-- Toggler for small screens -->
            <button class="btn btn-primary d-lg-none" @click="toggleSidebar">
              <span class="navbar-toggler-icon"></span>
            </button>

          </div>
        </div>

        <div class="row">
          <ChatContainer></ChatContainer>
        </div>
      </div>

      <!-- Sidebar for xs to md screens, full screen -->
      <div v-if="showSidebar" class="mobile-sidebar d-lg-none p-3">
        <!-- Sidebar content -->
        <div class="row">
          <div class="col">
            <ChatHeader :show-name="true"></ChatHeader>
          </div>
        </div>
        <div class="row">
          <div class="col">
            <ThreadList></ThreadList>
          </div>
        </div>
        <!-- Add more buttons as needed -->
        <button class="btn btn-lg btn-primary" @click="toggleSidebar">
          <i class="bi bi-x"></i>
          Close
        </button>

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
    return {
      showSidebar: false,
    };
  },
  computed: {
    assistantName() {
      return this.store.selectedAssistant.name;
    },
  },
  methods: {
    toggleSidebar() {
      this.showSidebar = !this.showSidebar;
    },
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

.mobile-sidebar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #000000;
  z-index: 1050; /* Higher than the default navbar z-index to overlay on top */
  overflow-y: auto;
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


hr {
  color: #ffffff;
}


.logo-small {
  height: 3rem;
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

