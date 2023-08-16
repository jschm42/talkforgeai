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
        <div v-if="isEditMode">
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
import ChatSession from '@/store/to/chat-session';
import {useChatStore} from '@/store/chat-store';
import {format} from 'date-fns';
import ChatService from '@/service/chat.service';

const chatService = new ChatService();

export default {
  name: 'ChatHistoryEntry',
  props: {
    entry: ChatSession,
  },
  data() {
    return {
      isEditMode: false,
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
      return `${this.entry.id} - ${this.entry.description}`;
    },
    isSelected() {
      return this.entry.id === this.store.selectedSessionId;
    },
  },
  methods: {
    getEntryClass() {
      if (this.isSelected) {
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
      console.log('DELETE');
    },
    onClickEdit() {
      console.log('EDIT');
      this.isEditMode = true;
    },
    onClickConfirm() {
      this.oldTitle = this.title;
      this.isEditMode = false;
      chatService.updateSessionTitle(this.store.sessionId, this.title);
    },
    onClickCancel() {
      this.title = this.oldTitle;
      this.isEditMode = false;
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
