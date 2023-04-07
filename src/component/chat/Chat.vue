<template>
  <div class="flex-fill vertical-scrollbar no-horizontal-scrollbar">

    <div v-for="message in store.processedMessages">
      {{ message.content }}
    </div>

  </div>
  <!-- Input Section -->

  <ChatControl></ChatControl>

</template>

<script>
import ChatControl from './ChatControl.vue';
import chatStore from '../../store/chat.store';

export default {
  name: 'Chat',
  data() {
    return {
      store: chatStore,
    };
  },
  components: {ChatControl},

};
</script>

<style scoped>

</style>
