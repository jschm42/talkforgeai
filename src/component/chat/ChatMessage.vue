<template>
  <div :class="messageClass" class="card m-1 p-1 shadow">
    <div class="row">
      <div class="col-md-1">
        <i :class="avatarImageClass" class="bi role-icon"></i>
      </div>
      <div class="col-md-10">
        <div class="card-body">
          <div class="card-text" v-html="message.content"></div>
        </div>
      </div>
      <div class="col-md-1 text-end">
        <i class="bi bi-play-circle-fill message-icon" role="button" @click="playAudio"></i>
      </div>
    </div>

  </div>

</template>

<script>

import Role from '../../service/to/role';

export default {
  name: 'ChatMessage',
  props: {
    message: {
      role: String,
      content: String,
    },
  },
  computed: {
    messageClass() {
      return {
        'bg-info': this.message.role === Role.USER,
      };
    },
    isAssistant() {
      return this.message.role === Role.ASSISTANT;
    },
    isUser() {
      return this.message.role === Role.USER;
    },
    avatarImageClass() {
      return {
        'bi-robot': this.message.role === Role.ASSISTANT,
        'bi-person-fill': this.message.role === Role.USER,
      };
    },
  },
  methods: {
    async playAudio() {
      return window.chatAPI.textToSpeech(this.message.content);
    },
  },
};
</script>

<style scoped>
.role-user {

}

.role-assistant {

}

.role-icon {
  font-size: 2em;
}

.message-icon {
  font-size: 1.5em;
}

</style>
