<template>
  <div class="flex-fill vertical-scrollbar no-horizontal-scrollbar">

    <div v-for="message in chatState.processedMessages" v-html="message.content">
    </div>

  </div>
  <!-- Input Section -->

  <ChatControl></ChatControl>

</template>

<script>
import ChatControl from './ChatControl.vue';
import Store from '../../store/store';

export default {
  name: 'Chat',
  data() {
    return {
      chatState: Store.state.chat,
    };
  },
  components: {ChatControl},
  changed() {
    console.log('Chat-Component changed');
  },
};
</script>

<style scoped>

</style>
