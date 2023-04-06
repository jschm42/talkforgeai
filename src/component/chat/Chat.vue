<template>
  <div class="flex-fill vertical-scrollbar no-horizontal-scrollbar">

  </div>
  <!-- Input Section -->

  <ChatControl></ChatControl>

</template>

<script>
import ChatControl from './ChatControl.vue';

export default {
  name: 'Chat',
  components: {ChatControl},
  
};
</script>

<style scoped>

</style>
