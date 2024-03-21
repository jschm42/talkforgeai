<!--
  - Copyright (c) 2023-2024 Jean Schmitz.
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
import {useChatStore} from '@/store/chat-store';
import Role from '@/store/to/role';
import AssistantProperties, {TTSType} from '@/const/assistant.properties';
import {useAssistants} from '@/composable/use-assistants';
import {useHtmlToText} from '@/composable/use-html-to-text';
import {useTextToSpeech} from '@/composable/use-text-to-speech';

export default defineComponent({
  name: 'ChatMessageTextToSpeech',
  setup() {
    const chatStore = useChatStore(); // Call useMyStore() inside the setup function
    const assistants = useAssistants();
    const htmlToText = useHtmlToText();
    const textToSpeech = useTextToSpeech();
    return {chatStore, assistants, htmlToText, textToSpeech};
  },
  data() {
    return {
      audioState: AudioState.Stopped,
    };
  },
  props: {
    message: Object,
  },
  computed: {
    getTTSType() {
      return this.chatStore.selectedAssistant.properties[AssistantProperties.TTS_TYPE];
    },
    isDisabled() {
      return this.chatStore.selectedAssistant.properties[AssistantProperties.TTS_TYPE] ===
          TTSType.DISABLED;
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

      const plainText = this.htmlToText.removeHtml(this.message.content[0].text.value);
      this.audioState = AudioState.Loading;

      if (this.getTTSType === TTSType.SPEECHAPI) {
        await this.speakSpeechApi(plainText);
      } else {
        await this.speakElevenlabs(plainText);
      }
    },
    async speakElevenlabs(plainText) {
      try {
        const audioBlob = await this.textToSpeech.speakElevenlabs(plainText,
            this.message.assistant_id);
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
      console.log(`Speaking using SpeechAPI: '${plainText}'`);
      this.audioState = AudioState.Playing;
      const assistant = this.assistants.getAssistantById(this.message.assistant_id);
      await this.textToSpeech.speakSpeechAPI(plainText, assistant);
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
  <v-btn v-if="audioState === 'stopped'" :hidden="isDisabled" class="fs-4" icon="mdi-play-circle"
         @click="playAudio"></v-btn>
  <v-btn v-if="audioState === 'playing'" :hidden="isDisabled" class="fs-4" icon="mdi-stop-circle"
         @click="stopAudio"></v-btn>
  <v-progress-circular v-if="audioState === 'loading'" :hidden="isDisabled" color="primary"
                       indeterminate></v-progress-circular>
</template>
