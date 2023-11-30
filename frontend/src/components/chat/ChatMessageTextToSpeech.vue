<!--
  - Copyright (c) 2023 Jean Schmitz.
  -
  - Licensed under the Apache License, Version 2.0 (the "License");
  - you may not use this file except in compliance with the License.
  - You may obtain a copy of the License at
  -
  -     http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing, software
  - distributed under the License is distributed on an "AS IS" BASIS,
  - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  - See the License for the specific language governing permissions and
  - limitations under the License.
  -->

<script>
import {defineComponent} from 'vue';
import TtsService from '@/service/tts.service';
import {useChatStore} from '@/store/chat-store';
import Role from '@/store/to/role';
import HtmlToTextService from '@/service/html-to-text.service';
import {ThreadMessage} from '@/store/to/thread';
import AssistantProperties, {TTSType} from '@/service/assistant.properties';

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
    message: ThreadMessage,
  },
  computed: {
    getTTSType() {
      return this.store.selectedAssistant.properties[AssistantProperties.TTS_TYPE];
    },
    isDisabled() {
      return this.store.selectedAssistant.properties[AssistantProperties.TTS_TYPE] === TTSType.DISABLED;
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

      const plainText = htmlToTextService.removeHtml(this.message.content[0].text.value);
      this.audioState = AudioState.Loading;

      if (this.getTTSType === TTSType.SPEECHAPI) {
        await this.speakSpeechApi(plainText);
      } else {
        await this.speakElevenlabs(plainText);
      }
    },
    async speakElevenlabs(plainText) {
      try {
        const audioBlob = await ttsService.speakElevenlabs(plainText, this.store.selectedAssistant);
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
      await ttsService.speakSpeechAPI(plainText, this.store.selectedAssistant);
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
  <i v-if="audioState === 'stopped'" :hidden="isDisabled" class="bi bi-play-circle message-icon play-icon"
     role="button" @click="playAudio"></i>

  <i v-if="audioState === 'playing'" :hidden="isDisabled" class="bi bi-stop-circle message-icon play-icon"
     role="button" @click="stopAudio"></i>

  <div v-if="audioState === 'loading'" :hidden="isDisabled" class="spinner-border spinner-border-sm loading-icon"
       role="status">
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
