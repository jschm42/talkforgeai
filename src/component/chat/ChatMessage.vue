<template>
  <div :class="messageClass" class="card m-1 p-1 shadow">
    <div class="row">
      <div class="col-md-1">
        <i :class="avatarImageClass" class="bi role-icon"></i>
        <span class="spinner-grow" role="status"></span>
      </div>
      <div class="col-md-10">
        <div class="card-body">
          <div class="card-text" v-html="message.content"></div>
        </div>
      </div>
      <div class="col-md-1 text-end">

        <i v-if="audioState === 'stopped'" class="bi bi-play-circle-fill message-icon" role="button"
           @click="playAudio"></i>

        <i v-if="audioState === 'paused'" class="bi bi-play-circle-fill message-icon" role="button"
           @click="playAudio"></i>

        <i v-if="audioState === 'playing'" class="bi bi-pause-circle-fill message-icon" role="button"
           @click="pauseAudio"></i>

        <div v-if="audioState === 'loading'" class="spinner-border spinner-icon" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>

      </div>
    </div>

  </div>

</template>

<script>

import Role from '../../service/to/role';

const AudioState = {
  Loading: 'loading',
  Playing: 'playing',
  Paused: 'paused',
  Stopped: 'stopped',
};

export default {
  name: 'ChatMessage',
  data() {
    return {
      audioState: AudioState.Stopped,
    };
  },
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
    pauseAudio() {
      console.log('Audio paused');
      this.audioState = AudioState.Paused;
    },
    stopAudio() {
      console.log('Audio stopped');
      this.audioState = AudioState.Stopped;
    },
    async playAudio() {
      console.log('Playing audio');
      this.audioState = AudioState.Loading;
      try {
        const audioBlob = await window.chatAPI.textToSpeech(this.message.content);
        const audioUrl = URL.createObjectURL(audioBlob);
        const audio = new Audio(audioUrl);
        audio.addEventListener('ended', () => {
          this.audioState = AudioState.Stopped;
          console.log('Audio Stream ended');
        });

        this.audioState = AudioState.Playing;
        await audio.play();
      } catch (error) {
        console.error('Error loading audio stream.', error);
        this.audioState = AudioState.Stopped;
      }
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

.spinner-icon {
  font-size: 1.0em;
}

.code-word {
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 0.25rem;
  /*font-family: "Source Code Pro", monospace;*/
  font-family: 'Courier New', monospace
}


</style>
