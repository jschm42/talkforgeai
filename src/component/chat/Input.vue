<template>
  <div class="input-group">
    <div id="new-session-button" class="input-group-text">
      <i class="bi bi-x-circle" role="button" style="font-size: 2em" @click="clearChat"></i>
    </div>
    <textarea v-model="prompt" class="form-control shadow" placeholder="Enter text..."
              rows="5"></textarea>
    <div id="submit-button" class="input-group-text">
      <i class="bi bi-send-check-fill" role="button" style="font-size: 2em" @click="submit"></i>
    </div>
  </div>
</template>

<script>

export default {
  name: 'Input',
  data() {
    return {
      prompt: '',
    };
  },
  methods: {
    submit() {
      window.chatAPI.submitPrompt(this.prompt);
    },

    setupResponseListener() {
      window.chatAPI.listenToPromptReply((processedMessage) => {
        // Handle the response from the main process, e.g., update the component's data
        console.log('Received processed Message', processedMessage);
      });
    },
  },
  mounted() {
    this.setupResponseListener();
  },
};
</script>

<style scoped>

</style>
