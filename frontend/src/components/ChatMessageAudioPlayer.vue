<script>
import {defineComponent} from 'vue';
import TtsService from '@/service/tts.service';
import {useChatStore} from '@/store/chat-store';

export default defineComponent({
  name: 'ChatMessageAudioPlayer',
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    return {store};
  },
  data() {
    return {
      audioState: AudioState.Stopped,
    };
  },
  props: {
    text: String,
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
        //const properties = toRaw(this.store.getElevenLabsProperties());
        //const audioBlob = await window.chatAPI.textToSpeech(this.message.content, properties);
        const audioBlob = await ttsService.speak(this.text);
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
});

const AudioState = {
  Loading: 'loading',
  Playing: 'playing',
  Paused: 'paused',
  Stopped: 'stopped',
};

const ttsService = new TtsService();


</script>

<template>
  <i v-if="audioState === 'stopped'" class="bi bi-play-circle-fill message-icon" role="button"
     @click="playAudio"></i>

  <i v-if="audioState === 'paused'" class="bi bi-play-circle-fill message-icon" role="button"
     @click="playAudio"></i>

  <i v-if="audioState === 'playing'" class="bi bi-pause-circle-fill message-icon" role="button"
     @click="pauseAudio"></i>

  <div v-if="audioState === 'loading'" class="spinner-border spinner-border-sm" role="status">
    <span class="visually-hidden">Loading...</span>
  </div>
</template>

<style scoped>

</style>
