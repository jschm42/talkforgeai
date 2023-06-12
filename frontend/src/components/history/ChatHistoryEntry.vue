<template>

  <a :class="getEntryClass(entry.id)"
     class="list-group-item list-group-item-action py-3 lh-sm"
     @click="$emit('entrySelected', entry.id)">
    <div class="d-flex w-100 align-items-center justify-content-between">
      <strong :title="getTitle" class="mb-1 text-truncate">{{ entry.title }}</strong>
    </div>
    <div class="col-10 mb-1 small">{{ formatTimestamp(entry.createdAt) }}</div>
  </a>

</template>

<script>
import ChatSession from '@/store/to/chat-session';
import {useChatStore} from '@/store/chat-store';
import {format} from 'date-fns';

export default {
  name: 'ChatHistoryEntry',
  props: {
    entry: ChatSession,
  },
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    return {store};
  },
  computed: {
    getTitle() {
      return `${this.entry.id} - ${this.entry.description}`;
    },
  },
  methods: {
    getEntryClass(sessionId) {
      if (sessionId === this.store.selectedSessionId) {
        return 'bg-primary';
      }
      return '';
    },
    formatTimestamp(timestamp) {
      if (timestamp) {
        return format(new Date(timestamp), 'MM/dd/yyyy');
      }
    },
  },
};
</script>

<style scoped>

</style>
