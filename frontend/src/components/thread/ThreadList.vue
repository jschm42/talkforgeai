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
  <div class="full-height vertical-scrollbar shadow">

    <div class="row align-items-center">
      <div class="col-1">
        <i class="bi bi-box-arrow-left exit-button" role="button" @click.prevent="onClickBack"></i>
      </div>
      <div class="col-11">
        <button class="col-12" @click.prevent="onNewThread">New Chat</button>
      </div>
    </div>

    <div class="list-group list-group-flush border-bottom">

      <div v-for="entry in allSessionEntries" :key="entry.id">
        <ThreadEntry :entry="entry" @entrySelected="onEntrySelected"/>
      </div>

    </div>

  </div>
</template>


<script>
import {useChatStore} from '@/store/chat-store';
import ThreadEntry from '@/components/thread/ThreadEntry.vue';

export default {
  name: 'ThreadList',
  components: {ThreadEntry},
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function

    return {store};
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
      if (this.store.threadId !== threadId) {
        console.log('Loading thread', threadId);
        this.store.threadId = threadId;
        await this.store.retrieveMessages(threadId);
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
</style>
