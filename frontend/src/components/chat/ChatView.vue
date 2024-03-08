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
    <v-container fluid>

      <v-row>
        <!-- Sidebar visible on lg and higher screens -->
        <v-col class="d-none d-md-block sidebar" cols="3" md="3" sm="4" xl="2">

          <v-container class="d-flex flex-column nav-container">
            <v-row class="flex-grow-0">
              <!-- Row 1 with flex-grow 1 -->
              <v-col>
                <ChatHeader></ChatHeader>
              </v-col>
            </v-row>
            <v-row class="flex-grow-0">
              <!-- Row 2 with flex-grow 2 -->
              <v-col>
                <v-btn class="mx-2" prepend-icon="mdi-arrow-left-bold-box-outline"
                       @click.prevent="onClickBack">
                  Back
                </v-btn>
                <v-btn prepend-icon="mdi-heart" @click.prevent="onNewThread">
                  New Chat
                </v-btn>
              </v-col>
            </v-row>
            <v-row class="flex-grow-1 scrollable-container">
              <!-- Row 3 with flex-grow 3 -->
              <v-col>
                <ThreadList></ThreadList>
              </v-col>
            </v-row>
          </v-container>

          <!-- Add more buttons as needed -->
        </v-col>
        <v-col>
          <!-- Main Content -->
          <v-row class="toolbar-header p-2 d-lg-none">
            <v-col class="d-flex">
              <v-toolbar
                  dark
                  image="https://cdn.vuetifyjs.com/images/backgrounds/vbanner.jpg"
                  prominent
              >
                <v-btn prepend-icon="mdi-arrow-left-bold-box-outline" @click.prevent="onClickBack">
                </v-btn>
                <v-toolbar-title>{{ assistantName }}</v-toolbar-title>
                <v-app-bar-nav-icon @click="toggleSidebar"></v-app-bar-nav-icon>
              </v-toolbar>

            </v-col>
          </v-row>

          <ChatContainer></ChatContainer>
        </v-col>

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
              <v-toolbar>
                <div class="btn btn-outline-light d-flex align-items-center me-2 d-none d-lg-block"
                     @click.prevent="onClickBack">
                  <i class="bi bi-box-arrow-left me-2"></i>
                  Back
                </div>

                <v-spacer></v-spacer>

                <div class="btn btn-outline-light d-flex align-items-center"
                     @click.prevent="onNewThread">
                  <i class="bi bi-plus-circle-fill mx-2"></i>
                  New Chat
                </div>
              </v-toolbar>
            </div>
          </div>
          <div class="row my-2">
            <div class="col">
              <ThreadList></ThreadList>
            </div>
          </div>
          <!-- Add more buttons as needed -->
          <button class="btn btn-outline-light" @click="toggleSidebar">
            <i class="bi bi-x"></i>
            Close
          </button>

        </div>

      </v-row>
    </v-container>
  </v-app>
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
    onNewThread() {
      this.store.newThread();
    },
    onClickBack() {
      this.$router.push('/');
    },
    toggleSidebar() {
      this.showSidebar = !this.showSidebar;
    },
    async fetchData() {
      try {
        this.store.selectedAssistant.image_path = '';
        if (this.store.assistantList.length === 0) {
          await this.store.retrieveAssistants();
        }
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

.assistant-name {
  font-size: 2rem;
  font-weight: bold;
  color: white;
  text-shadow: #FC0 1px 0 10px;
}

.scrollable-container {
  overflow-y: auto;
  height: 0vh; /* Adjust this value according to your needs */
}

.nav-container {
  height: 97vh;
}

</style>

