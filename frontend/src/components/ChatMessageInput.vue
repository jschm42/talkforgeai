<template>
  <div class="input-group">
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
      this.isInputLocked = true;

      await this.store.streamPrompt(this.prompt);
      await this.store.loadIndex(this.store.selectedPersona.personaId);

      this.$emit('submitResultReceived');
      this.prompt = '';
      this.isInputLocked = false;
      /*
      await this.$nextTick(() => {
        this.$refs.promptInputArea.focus();
      });
       */
    },
  },
};
</script>

<style scoped>

</style>
