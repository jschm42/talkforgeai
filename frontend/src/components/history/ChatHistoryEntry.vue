<template>

  <a :class="getEntryClass(entry.id)"
     class="list-group-item list-group-item-action py-1 px-1 lh-sm"
     @click="$emit('entrySelected', entry.id)">

    <div class="row p-0 m-0">
      <div class="col-10 text-start">
        <h5 class="overflow-hidden">{{ entry.title }}</h5>
        <p class="small overflow-hidden">{{ formatTimestamp(entry.createdAt) }}</p>
      </div>
      <div class="col-2 d-inline-flex flex-row-reverse my-2">
        <i class="bi bi-trash" role="button"></i>
        <i class="bi bi-pencil mx-3" role="button"></i>
      </div>
    </div>
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
      console.log('GET ENTRY CLASS 1', sessionId);
      console.log('GET ENTRY CLASS 2', this.store.selectedSessionId);
      if (sessionId === this.store.selectedSessionId) {
        return 'bg-primary';
      }
      return '';
    },
    formatTimestamp(timestamp) {
      if (timestamp) {
        return format(new Date(timestamp), 'MM/dd/yyyy HH:mm:ss');
      }
    },
  },
};
</script>

<style scoped>
i {
  float: right;
}

</style>
