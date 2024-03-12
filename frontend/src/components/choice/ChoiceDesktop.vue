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
  <v-navigation-drawer absolute permanent>
    <v-list-item>
      <div class="d-flex flex-grow-1 align-items-start">
        <img alt="Talkforge AI" class="logo" src="@/assets/logo.png"
             title="Talkforge AI">
      </div>
    </v-list-item>
    <!--      <v-divider></v-divider>-->
    <v-list density="comfortable" lines="false" nav>
      <v-list-subheader>Main Desktop</v-list-subheader>
      <v-list-item color="primary" title="Create Persona" variant="elevated"
                   @click.prevent="onCreateNewPersona">
        <template v-slot:prepend>
          <v-icon icon="mdi-plus-thick"></v-icon>
        </template>
      </v-list-item>
    </v-list>
  </v-navigation-drawer>

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
