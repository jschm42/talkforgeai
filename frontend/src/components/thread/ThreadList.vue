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
  <v-toolbar>
    <div class="btn btn-outline-light d-flex align-items-center me-2 d-none d-lg-block"
         @click.prevent="onClickBack">
      <i class="bi bi-box-arrow-left me-2"></i>
      Back
    </div>

    <v-spacer></v-spacer>

    <div class="btn btn-outline-light d-flex align-items-center" @click.prevent="onNewThread">
      <i class="bi bi-plus-circle-fill mx-2"></i>
      New Chat
    </div>
  </v-toolbar>

  <v-container class="scrollable-container">
    <v-row>
      <div class="list-group list-group-flush border-bottom">
        <div v-for="entry in allSessionEntries" :key="entry.id">
          <ThreadEntry :entry="entry" @entrySelected="onEntrySelected"/>
        </div>

      </div>
    </v-row>
  </v-container>
</template>


<script>
import {useChatStore} from '@/store/chat-store';
import ThreadEntry from '@/components/thread/ThreadEntry.vue';
import {useAppStore} from '@/store/app-store';
import hljs from 'highlight.js';
import {nextTick} from 'vue';

export default {
  name: 'ThreadList',
  components: {ThreadEntry},
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
    allSessionEntries() {
      return this.store.threads;
    },
  },
  methods: {
    async onEntrySelected(threadId) {
      try {
        if (this.store.threadId !== threadId) {
          this.store.threadId = threadId;
          await this.store.retrieveMessages(threadId);
          await nextTick();
          hljs.highlightAll();
        }
      } catch (error) {
        this.appStore.handleError(error);
      }
    },
    onNewThread() {
      this.store.newThread();
    },
    onClickBack() {
      this.$router.push('/');
    },
  },
  changed() {
    console.log('History-Component changed');
  },
};
</script>

<style scoped>
.exit-button {
  font-size: 2em;
  color: white;
}

.scrollable-container {
  overflow-y: auto;
  height: 50vh; /* Adjust this value according to your needs */
}
</style>
