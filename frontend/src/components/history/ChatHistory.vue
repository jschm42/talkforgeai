<template>
  <div class="p-3 full-height vertical-scrollbar shadow">

    <button class="col-12 my-2" @click="onNewSession">New Chat</button>

    <div class="list-group list-group-flush border-bottom">

      <div v-for="entry in allSessionEntries" :key="entry.id">
        <ChatHistoryEntry :entry="entry" @entrySelected="onEntrySelected"/>
      </div>

    </div>

  </div>
</template>


<script>
import {useChatStore} from '@/store/chat-store';
import ChatHistoryEntry from '@/components/history/ChatHistoryEntry.vue';

export default {
  name: 'ChatHistory',
  components: {ChatHistoryEntry},
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
      console.log('SESSION ENTRIES', this.store.sessions);
      return this.store.sessions;
    },
  },
  methods: {
    async onEntrySelected(sessionId) {
      console.log('Loading chat session', sessionId);
      this.store.selectedSessionId = sessionId;
      await this.store.loadChatSession(sessionId);
    },
    onNewSession() {
      this.store.newSession();
    },
  },
  changed() {
    console.log('History-Component changed');
  },
};
</script>

<style scoped>

</style>
