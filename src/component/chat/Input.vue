<template>
  <div class="input-group">
    <div id="new-session-button" class="input-group-text">
      <i class="bi bi-x-circle" role="button" style="font-size: 2em" @click="clearChat"></i>
    </div>
    <textarea v-model="prompt" class="form-control shadow" placeholder="Enter text..." rows="5"
              @keyup.enter.exact="submit"></textarea>
    <div id="submit-button" class="input-group-text">
      <i class="bi bi-send-check-fill" role="button" style="font-size: 2em" @click="submit"></i>
    </div>
  </div>
</template>


<script lang="ts">

import {useChatStore} from '../../store/piniaStore';

export default {
  name: 'Input',
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function

    return {store};
  },
  data() {
    return {
      prompt: '',
      chatState: this.store.chat,
    };
  },
  methods: {
    submit() {
      this.store.submitPrompt(this.prompt);
    },
  },
  mounted() {

  },
  changed() {
    console.log('Input-Component changed');
  },
};
</script>

<style scoped>

</style>
