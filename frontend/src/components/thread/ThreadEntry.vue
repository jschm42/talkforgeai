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

  <div :class="getEntryClass()"
       class="list-group-item list-group-item-action lh-sm">

    <div class="d-flex flex-row text-start p-1">
      <div class="flex-grow-1 mx-1 text-truncate" role="button" @click.prevent="onEntrySelected">
        <input v-if="isEditMode" v-model="title" class="title-input"/>
        <p v-else class="text-truncate thread-title">{{ title }}</p>
        <!--        <p class="small overflow-hidden">{{ formatTimestamp(entry.createdAt) }}</p>-->
      </div>
      <div class="flex-shrink-0">
        <div v-if="isEditMode || isDeleteMode" class="confirm-buttons">
          <i class="bi bi-check" role="button" @click="onClickConfirm"></i>
          <i class="bi bi-x" role="button" @click="onClickCancel"></i>
        </div>
        <div v-else-if="isSelected" class="edit-buttons">
          <i class="bi bi-pencil mx-2" role="button" @click="onClickEdit"></i>
          <i class="bi bi-trash" role="button" @click="onClickDelete"></i>
        </div>
      </div>
    </div>
  </div>

</template>

<script>
import {useChatStore} from '@/store/chat-store';
import {format} from 'date-fns';

export default {
  name: 'ThreadEntry',
  props: {
    entry: Object,
  },
  data() {
    return {
      title: '',
      oldTitle: '',
    };
  },
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    return {store};
  },
  computed: {
    isEditMode() {
      return this.store.threadEditMode && this.isSelected;
    },
    isDeleteMode() {
      return this.store.threadDeleteMode && this.isSelected;
    },
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
      this.store.threadDeleteMode = true;
    },
    onClickEdit() {
      this.store.threadEditMode = true;
    },
    onEntrySelected() {
      if (!this.isSelected) {
        this.store.threadDeleteMode = false;
        this.store.threadEditMode = false;
      }
      this.$emit('entrySelected', this.entry.id);
    },
    async onClickConfirm() {
      if (this.store.threadEditMode) {
        this.oldTitle = this.title;
        this.store.threadEditMode = false;
        await this.store.updateThreadTitle(this.store.threadId, this.title);
      } else if (this.store.threadDeleteMode) {
        this.store.threadDeleteMode = false;
        await this.store.deleteThread(this.store.threadId);
      }
    },
    onClickCancel() {
      this.title = this.oldTitle;
      this.store.threadEditMode = false;
      this.store.threadDeleteMode = false;
    },
  },
  mounted() {
    this.title = this.entry.title;
    this.oldTitle = this.entry.title;
  },
};
</script>

<style scoped>

.title-input {
  width: 100%;
  border: none;
  background-color: transparent;
  color: white;
  font-size: 1rem;
  text-align: left;
  outline: none;
}

.confirm-buttons {
  font-size: 1.7rem;
}

.edit-buttons {
  font-size: 1.5rem;
}

.thread-title {
  font-size: 1rem;
}


@media only screen and (min-width: 768px ) {
  .edit-buttons {
    font-size: 1.2rem;
  }
}

</style>
