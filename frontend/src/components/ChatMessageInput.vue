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
    submit() {
      this.isInputLocked = true;

      this.store.currentStatusMessage = 'Thinking...';
      try {
        this.store.streamPrompt(this.prompt, () => {
          this.$emit('chunkUpdateReceived');
        });
        this.store.currentStatusMessage = 'Updating...';
        //await this.store.loadIndex(this.store.selectedPersona.personaId);
        this.store.currentStatusMessage = '';
      } catch (error) {
        console.error(error);
        this.store.currentStatusMessage = 'Error: ' + error;
      } finally {
        this.isInputLocked = false;
      }

      console.log('Sending submitResultReceived');
      this.$emit('submitResultReceived');
      this.prompt = '';
    },
  },
};
</script>

<style scoped>

</style>
