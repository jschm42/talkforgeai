<template>
  <div class="d-flex flex-column full-height">
    <PersonaCompactInfo :persona="store.selectedPersona" :show-properties="true"></PersonaCompactInfo>

    <div ref="entries" class="flex-grow-1 vertical-scrollbar no-horizontal-scrollbar">
      <ChatMessage v-for="(message, index) in store.messages" ref="chatMessageRef" v-bind:key="index"
                   :message="message" :messageIndex="index"></ChatMessage>
    </div>
    <!-- Input Section -->

    <div class="flex-shrink-0">
      <ChatControl @submit-result-received="submitResultReceived"
                   @chunk-update-received="chunkUpdateReceived"></ChatControl>
    </div>
  </div>
</template>

<script>
import ChatControl from './ChatControl.vue';
import ChatMessage from './ChatMessage.vue';
import {useChatStore} from '@/store/chat-store';
import PersonaCompactInfo from '@/components/PersonaCompactInfo.vue';

export default {
  name: 'ChatContainer',
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function

    return {
      store,
    };
  },
  data() {
    return {};
  },
  components: {PersonaCompactInfo, ChatControl, ChatMessage},
  methods: {
    async submitResultReceived() {
      console.log('Submit Result Received');

      if (this.store.chat.autoSpeak) {
        console.log('Auto speaking last Chat-Message.');
        const lastChatMessage = this.$refs.chatMessageRef.slice(-1)[0];
        await lastChatMessage.playAudio();
      }
    },
    chunkUpdateReceived() {
      // Scroll to bottom
      console.log('Scrolling to bottom!!!!');
      this.$refs.entries.scrollTop = this.$refs.entries.scrollHeight;
    },
  },
  updated() {
    console.log('Chat-Component updated');
  },
  mounted() {
    // const wsService = new WebSocketService();

    // wsService.statusUpdateHandler = (data) => {
    //   this.store.updateStatus(data.sessionId, data.status);
    // };

    // wsService.functionCallHandler = (data) => {
    //   this.store.messages = [...this.store.messages, data.message];
    //   this.store.sendFunctionConfirm(data.sessionId);
    // };

    // const wsClient = wsService.createClient();
    // wsClient.activate();
    //
    // document.addEventListener('visibilitychange', function() {
    //   if (document.hidden) {
    //     console.log('Tab is not active');
    //     wsClient.deactivate();
    //   } else {
    //     console.log('Tab is active');
    //     wsClient.activate();
    //   }
    // });

  },

};
</script>

<style scoped>
.vertical-scrollbar {
  overflow-y: auto;
}

.no-horizontal-scrollbar {
  overflow-x: hidden;
}

.full-height {
  height: 100vh;
}

</style>
