<script>
import {defineComponent} from 'vue';
import TtsService from '@/service/tts.service';
import {useChatStore} from '@/store/chat-store';
import ChatMessage from '@/store/to/chat-message';
import Role from '@/store/to/role';
import HtmlToTextService from '@/service/html-to-text.service';
import PersonaProperties, {TTSType} from '@/service/persona.properties';

const ttsService = new TtsService();
const htmlToTextService = new HtmlToTextService();

export default defineComponent({
  name: 'ChatMessageTextToSpeech',
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
  computed: {
    getTTSType() {
      return this.store.selectedPersona.properties[PersonaProperties.TTS_TYPE];
    },
  },
  beforeUnmount() {
    this.stopAudio();
  },
  methods: {
    stopAudio() {
      console.log('Audio stopped');

      if (this.getTTSType === TTSType.SPEECHAPI) {
        window.speechSynthesis.cancel();
      }

      this.audioState = AudioState.Stopped;
    },
    async playAudio() {
      if (this.message.role !== Role.ASSISTANT) {
        console.log('Not a speakable message.');
        return;
      }

      const plainText = htmlToTextService.removeHtml(this.message.content);
      this.audioState = AudioState.Loading;

      if (this.getTTSType === TTSType.SPEECHAPI) {
        await this.speakSpeechApi(plainText);
      } else {
        await this.speakElevenlabs(plainText);
      }
    },
    async speakElevenlabs(plainText) {
      try {
        const audioBlob = await ttsService.speakElevenlabs(plainText, this.store.selectedPersona);
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
    async speakSpeechApi(plainText) {
      console.log('Speaking using SpeechAPI...');
      this.audioState = AudioState.Playing;
      await ttsService.speakSpeechAPI(plainText, this.store.selectedPersona);
      console.log('Stopped...');
      this.audioState = AudioState.Stopped;
    },
  },
});

const AudioState = {
  Loading: 'loading',
  Playing: 'playing',
  Stopped: 'stopped',
};


</script>

<template>
  <i v-if="audioState === 'stopped'" class="bi bi-play-circle message-icon play-icon" role="button"
     @click="playAudio"></i>

  <i v-if="audioState === 'playing'" class="bi bi-stop-circle message-icon play-icon" role="button"
     @click="stopAudio"></i>

  <div v-if="audioState === 'loading'" class="spinner-border spinner-border-sm loading-icon" role="status">
    <span class="visually-hidden">Loading...</span>
  </div>
</template>

<style scoped>
.play-icon {
  font-size: 2em;
}

.loading-icon {
  width: 2em;
  height: 2em;
}
</style>
