<template>
  <div class="p-3 full-height vertical-scrollbar shadow">

    <!--<Toolbar></Toolbar>-->

    <div class="list-group list-group-flush border-bottom">

      <div v-for="entry in allIndexEntries" :key="entry.sessionId">
        <a id='{{entry.sessionId}}' :class="getEntryClass(entry.sessionId)"
           class="list-group-item list-group-item-action py-3 lh-sm {{entry.active}}"
           @click="onEntrySelected(entry.sessionId)">
          <div class="d-flex w-100 align-items-center justify-content-between">
            <strong :title="entry.sessionId" class="mb-1 text-truncate">{{ entry.title }}</strong>
          </div>
          <!--                    <div class="col-10 mb-1 small">{{ entry.timestamp }}</div>-->
        </a>
      </div>

    </div>

  </div>
</template>


<script>
import Toolbar from './Toolbar.vue';
import {useChatStore} from '@/store/chat-store';

export default {
  name: 'History',
  components: {Toolbar},
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
    allIndexEntries() {
      return this.store.index.entries;
    },
  },
  methods: {
    getEntryClass(sessionId) {
      if (sessionId === this.store.session.sessionId) {
        return 'bg-primary';
      }
      return '';
    },
    load() {
      this.store.loadIndex();
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
