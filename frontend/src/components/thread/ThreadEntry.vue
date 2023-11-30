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

  <a :class="getEntryClass()"
     class="list-group-item list-group-item-action py-1 px-1 lh-sm"
     @click="$emit('entrySelected', entry.id)">

    <div class="row p-0 m-0">
      <div class="col-10 text-start">
        <input v-if="isEditMode" v-model="title"/>
        <h5 v-else class="overflow-hidden">{{ title }}</h5>
        <p class="small overflow-hidden">{{ formatTimestamp(entry.createdAt) }}</p>
      </div>
      <div class="col-2 d-inline-flex flex-row-reverse my-2">
        <div v-if="isEditMode || isDeleteMode">
          <i class="bi bi-check" role="button" @click="onClickConfirm"></i>
          <i class="bi bi-x" role="button" @click="onClickCancel"></i>
        </div>
        <div v-else-if="isSelected">
          <i class="bi bi-trash" role="button" @click="onClickDelete"></i>
          <i class="bi bi-pencil" role="button" @click="onClickEdit"></i>
        </div>
      </div>
    </div>
  </a>

</template>

<script>
import {useChatStore} from '@/store/chat-store';
import {format} from 'date-fns';
import Thread from '@/store/to/thread';

export default {
  name: 'ChatHistoryEntry',
  props: {
    entry: Thread,
  },
  data() {
    return {
      isEditMode: false,
      isDeleteMode: false,
      title: '',
      oldTitle: '',
    };
  },
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    return {store};
  },
  computed: {
    getTitle() {
      return `${this.entry.id} - ${this.entry.title}`;
    },
    isSelected() {
      return this.entry.id === this.store.threadId;
    },
  },
  methods: {
    getEntryClass() {
      if (this.entry.id === this.store.threadId) {
        return 'bg-primary';
      }
      return '';
    },
    formatTimestamp(timestamp) {
      if (timestamp) {
        return format(new Date(timestamp), 'MM/dd/yyyy HH:mm:ss');
      }
    },
    onClickDelete() {
      this.isDeleteMode = true;
    },
    onClickEdit() {
      this.isEditMode = true;
    },
    async onClickConfirm() {
      if (this.isEditMode) {
        this.oldTitle = this.title;
        this.isEditMode = false;
        await this.store.updateSessionTitle(this.store.threadId, this.title);
      } else if (this.isDeleteMode) {
        this.isDeleteMode = false;
        await this.store.deleteChatSession(this.store.threadId);
      }
    },
    onClickCancel() {
      this.title = this.oldTitle;
      this.isEditMode = false;
      this.isDeleteMode = false;
    },
  },
  mounted() {
    this.title = this.entry.title;
    this.oldTitle = this.entry.title;
  },
};
</script>

<style scoped>
i {
  float: right;
}

</style>
