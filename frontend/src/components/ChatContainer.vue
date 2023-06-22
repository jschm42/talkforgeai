<template>
  <ChatHeader></ChatHeader>
  <div ref="entries" class="flex-fill vertical-scrollbar no-horizontal-scrollbar">

    <ChatMessage v-for="(message, index) in store.messages" ref="chatMessageRef"
                 v-bind:key="index" :message="message" :messageIndex="index"></ChatMessage>

  </div>
  <!-- Input Section -->

  <ChatControl @submit-submitResult-received="submitResultReceived"></ChatControl>

</template>

<script>
import ChatControl from './ChatControl.vue';
import ChatMessage from './ChatMessage.vue';
import ChatHeader from './ChatHeader.vue';
import {useChatStore} from '@/store/chat-store';
import WebSocketService from '@/service/web-socket.service';

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
  components: {ChatHeader, ChatControl, ChatMessage},
  methods: {
    submitResultReceived() {
      console.log('Submit Result Received');
      // Scroll to bottom
      this.$refs.entries.scrollTop = this.$refs.entries.scrollHeight;

      if (this.store.autoSpeak) {
        console.log('Auto speaking last Chat-Message.');
        const lastChatMessage = this.$refs.chatMessageRef.slice(-1)[0];
        lastChatMessage.playAudio();
      }
    },
  },
  updated() {
    console.log('Chat-Component updated');
  },
  mounted() {
    const wsService = new WebSocketService();

    // wsService.responseHandler = (data) => {
    //   this.store.messages = [...this.store.messages, data.message];
    //
    //   this.$nextTick(() => {
    //     hljs.highlightAll();
    //   });
    // };

    wsService.statusUpdateHandler = (data) => {
      this.store.updateStatus(data.sessionId, data.status);
    };

    // wsService.functionCallHandler = (data) => {
    //   this.store.messages = [...this.store.messages, data.message];
    //   this.store.sendFunctionConfirm(data.sessionId);
    // };

    const wsClient = wsService.createClient();
    wsClient.activate();

    document.addEventListener('visibilitychange', function() {
      if (document.hidden) {
        console.log('Tab is not active');
        wsClient.deactivate();
      } else {
        console.log('Tab is active');
        wsClient.activate();
      }
    });

  },

};
</script>

<style scoped>

</style>
