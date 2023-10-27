<template>
  <div class="d-flex flex-column full-height">

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
  components: {ChatControl, ChatMessage},
  mounted() {
    this.populateVoices();
  },
  methods: {
    async submitResultReceived() {
      console.log('Submit Result Received');

      if (this.store.chat.autoSpeak && this.store.isTTSEnabled) {
        const lastChatMessage = this.$refs.chatMessageRef.slice(-1)[0];
        console.log('Auto speaking last Chat-Message:');
        await lastChatMessage.playAudio();
      }
    },
    populateVoices() {
      const voices = speechSynthesis.getVoices();
      if (voices.length > 0) {
        console.log('Voices already loaded');
        this.speechApiVoices = voices;
      } else {
        console.log('Voices not loaded, waiting for onvoiceschanged');
        speechSynthesis.onvoiceschanged = () => {
          console.log('Voices loaded.');
        };
      }
    },
    chunkUpdateReceived() {
      // Scroll to bottom
      this.$refs.entries.scrollTop = this.$refs.entries.scrollHeight;
    },
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
