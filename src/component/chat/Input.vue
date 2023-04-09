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

<script>

import Store from '../../store/store';

export default {
  name: 'Input',
  data() {
    return {
      prompt: '',
      chatState: Store.state.chat,
    };
  },
  methods: {
    submit() {
      Store.mutations.chat.submit(this.prompt);
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
