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
import Store from '../../store/store';
import ChatMessage from './ChatMessage.vue';
import ChatHeader from './ChatHeader.vue';

export default {
  name: 'Chat',
  data() {
    return {
      chatState: Store.state.chat,
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
