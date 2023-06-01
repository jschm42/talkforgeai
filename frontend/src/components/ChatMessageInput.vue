<template>
  <div class="input-group">
    <div id="new-session-button" class="input-group-text">
      <i class="bi bi-x-circle" role="button" style="font-size: 2em" @click="clearChat"></i>
      <i class="bi bi-6-square" role="button" style="font-size: 2em" @click="testStream"></i>
    </div>
    <textarea ref="promptInputArea" v-model="prompt" :disabled="isInputLocked" class="form-control shadow"
              placeholder="Enter prompt..."
              rows="5"
              @keyup.enter.exact="submit"></textarea>
    <div id="submit-button" class="input-group-text">
      <i class="bi bi-send-check-fill" role="button" style="font-size: 2em" @click="submit"></i>
    </div>
  </div>
</template>


<script>

import {useChatStore} from '@/store/chat-store';

export default {
  name: 'ChatMessageInput',
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function

    return {store};
  },
  data() {
    return {
      prompt: '',
      isInputLocked: false,
      chatState: this.store.chat,
    };
  },
  methods: {
    async submit() {
      this.store.disableConfigHeader();
      this.isInputLocked = true;

      if (this.store.isEmptySession) {
        await this.store.newSession();
      }

      await this.store.submitPrompt(this.prompt);

      this.$emit('submitResultReceived');
      this.prompt = '';
      this.isInputLocked = false;
      /*
      await this.$nextTick(() => {
        this.$refs.promptInputArea.focus();
      });
       */
    },
    clearChat() {
      this.store.newSession();
    },
    async testStream() {
      await this.store.submitPrompt('Explain how to sort an array in java.');
    },
  },
};
</script>

<style scoped>

</style>
