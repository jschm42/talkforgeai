<script>
import {defineComponent} from 'vue';
import TtsService from '@/service/tts.service';
import {useChatStore} from '@/store/chat-store';
import ChatMessage from '@/store/to/chat-message';
import Role from '@/store/to/role';
import HtmlToTextService from '@/service/html-to-text.service';

const ttsService = new TtsService();
const htmlToTextService = new HtmlToTextService();

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
    message: ChatMessage,
  },
  mounted() {
    if (this.store.chat.autoSpeak) {
      this.playAudio().then(() => {
        console.log('Audio Played.');
      });
    }
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
      console.log('ROLE', this.message);
      if (this.message.role !== Role.ASSISTANT) {
        console.log('Not a speakable message.');
        return;
      }

      const plainText = htmlToTextService.removeHtml(this.message.content);
      console.log('PLAIN TEXT', plainText);

      console.log('Playing audio', this.store.selectedPersona);
      this.audioState = AudioState.Loading;
      try {
        const audioBlob = await ttsService.speak(plainText, this.store.selectedPersona.personaId);
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
