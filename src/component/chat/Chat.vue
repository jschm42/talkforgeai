<template>
  <ChatHeader></ChatHeader>
  <div ref="entries" class="flex-fill vertical-scrollbar no-horizontal-scrollbar">

    <ChatMessage v-for="message in chatState.processedMessages" ref="chatMessageRef" :message="message"></ChatMessage>

  </div>
  <!-- Input Section -->

  <ChatControl @submit-result-received="submitResultReceived"></ChatControl>

</template>

<script>
import ChatControl from './ChatControl.vue';
import ChatMessage from './ChatMessage.vue';
import ChatHeader from './ChatHeader.vue';
import {useChatStore} from '../../store/chat-store';

export default {
  name: 'Chat',
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function

    return {store};
  },
  data() {
    return {
      chatState: this.store.session,
    };
  },
  components: {ChatHeader, ChatControl, ChatMessage},
  methods: {
    submitResultReceived() {
      console.log('Submit Result Received');
      // Scroll to bottom
      this.$refs.entries.scrollTop = this.$refs.entries.scrollHeight;

      if (this.store.chat.autoSpeak) {
        console.log('Auto speaking last Chat-Message.');
        const lastChatMessage = this.$refs.chatMessageRef.slice(-1)[0];
        lastChatMessage.playAudio();
      }
    },
  },
  updated() {
    console.log('Chat-Component updated');
  },
};
</script>

<style scoped>

</style>
