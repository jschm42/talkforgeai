<template>
  <ChatHeader></ChatHeader>
  <div class="flex-fill vertical-scrollbar no-horizontal-scrollbar">

    <ChatMessage v-for="message in chatState.processedMessages" :message="message"></ChatMessage>

  </div>
  <!-- Input Section -->

  <ChatControl></ChatControl>

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
  changed() {
    console.log('Chat-Component changed');
  },
};
</script>

<style scoped>

</style>
