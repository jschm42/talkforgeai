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
      <div class="col-lg-2 d-none d-lg-block sidebar">
        <!-- Sidebar content -->
        <div class="d-flex flex-grow-1 align-items-start">
          <img alt="Talkforge AI" class="logo" src="@/assets/logo.png" title="Talkforge AI">
        </div>
        <hr>
        <ul class="nav nav-pills flex-column align-items-start">
          <li class="nav-item">
            <a class="nav-link text-white" href="#">
              <i class="bi bi-plus-circle-fill"></i>
              Create
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link text-white" href="#">
              <i class="bi bi-gear-fill"></i>
              Settings
            </a>
          </li>
        </ul>
        <!-- Add more buttons as needed -->
      </div>

      <!-- Main Content -->
      <div class="col-12 col-lg-10">

        <div class="row toolbar-header p-2">
          <div class="d-flex">

            <img alt="Talkforge AI" class="logo-small d-lg-none"
                 src="@/assets/logo-notext.png"
                 title="Talkforge AI">

            <h1 class="flex-grow-1 d-lg-none">Talkforge AI</h1>

            <!-- Toggler for small screens -->
            <button class="btn btn-primary d-lg-none" @click="toggleSidebar">
              <span class="navbar-toggler-icon"></span>
            </button>

          </div>
        </div>


        <!-- Your main content goes here -->
        <div class="row">
          <div class="d-flex flex-wrap justify-content-start scrollable-persona-list">
            <div v-for="assistant in assistantList" :key="assistant.id"
                 class="p-1 m-1 assistant-element">
              <assistant-element :assistant="assistant"></assistant-element>
            </div>
          </div>
        </div>
      </div>

      <!-- Sidebar for xs to md screens, full screen -->
      <div v-if="showSidebar" class="mobile-sidebar d-lg-none">
        <!-- Sidebar content -->
        <img alt="Talkforge AI" class="logo" src="@/assets/logo.png" title="Talkforge AI">
        <hr>
        <ul class="nav nav-pills flex-column align-items-start">
          <li class="nav-item">
            <a class="nav-link text-white" href="#">
              <i class="bi bi-plus-circle-fill"></i>
              Create
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link text-white" href="#">
              <i class="bi bi-gear-fill"></i>
              Settings
            </a>
          </li>
        </ul>
        <hr>
        <!-- Add more buttons as needed -->
        <button class="btn btn-lg btn-primary" @click="toggleSidebar">
          <i class="bi bi-x"></i>
          Close
        </button>
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
      showSidebar: false,
      isEntrySelected: false,
    };
  },
  computed: {
    assistantList() {
      return this.store.assistantList;
    },
  },
  methods: {
    toggleSidebar() {
      this.showSidebar = !this.showSidebar;
    },
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

/* Ensure sidebar is hidden on small screens and full screen when shown */
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
    width: 13rem;
    height: 21rem;
  }
}

@media only screen and (min-width: 375px) {
  .assistant-element {
    width: 10rem;
    height: 18rem;
  }
}

</style>
