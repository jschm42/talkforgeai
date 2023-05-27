<template>
  <div class="p-3 full-height vertical-scrollbar shadow">

    <!--<Toolbar></Toolbar>-->

    <div class="list-group list-group-flush border-bottom">

      <div v-for="entry in allSessionEntries" :key="entry.id">
        <a :class="getEntryClass(entry.id)"
           class="list-group-item list-group-item-action py-3 lh-sm"
           @click="onEntrySelected(entry.id)">
          <div class="d-flex w-100 align-items-center justify-content-between">
            <strong :title="entry.id" class="mb-1 text-truncate">{{ entry.title }}</strong>
          </div>
          <!--                    <div class="col-10 mb-1 small">{{ entry.timestamp }}</div>-->
        </a>
      </div>

    </div>

  </div>
</template>


<script>
import {useChatStore} from '@/store/chat-store';

export default {
  name: 'ChatHistory',
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function

    return {store};
  },
  data() {
    return {
      indexState: this.store.index,
      selectedIndexId: null,
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
    getEntryClass(sessionId) {
      if (sessionId === this.store.sessionId) {
        return 'bg-primary';
      }
      return '';
    },
    onEntrySelected(sessionId) {
      console.log('Index selected', sessionId);
      this.selectedIndexId = sessionId;
      this.store.loadChatSession(sessionId);
    },
  },
  mounted() {
    this.store.loadIndex();
  },
  changed() {
    console.log('History-Component changed');
  },
};
</script>

<style scoped>

</style>
