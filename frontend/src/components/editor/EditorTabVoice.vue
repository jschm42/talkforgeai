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
import {storeToRefs} from 'pinia';
import {useAssistantFormStore} from '@/store/persona-form-store';
import {useAppStore} from '@/store/app-store';
import AssistantProperties, {TTSType} from '@/const/assistant.properties';
import {useTextToSpeech} from '@/composable/use-text-to-speech';

export default defineComponent({
  name: 'PersonaTabVoice',
  data() {
    return {
      speechApiVoices: [],
      elevenLabsVoices: [],
      elevenLabsModels: [],
    };
  },
  setup() {
    const {assistantForm} = storeToRefs(useAssistantFormStore());
    const appStore = useAppStore();
    const textToSpeech = useTextToSpeech();

    return {assistantForm, appStore, textToSpeech};
  },
  computed: {
    TTSType() {
      return TTSType;
    },
    PersonaProperties() {
      return AssistantProperties;
    },
  },
  methods: {
    onChangeElevenLabsVoice() {
      console.log('onChangeElevenLabsVoice', this.assistantForm.properties.elevenlabs_voiceId);
      this.updateVoiceIdSelection();
    },
    updateVoiceIdSelection() {
      // If voiceId matches a voice in the list, select it
      const voice = this.elevenLabsVoices.find(
          voice => voice.voice_id ===
              this.assistantForm.properties[AssistantProperties.ELEVENLABS_VOICEID]);
      // If voice ist not found, set select element to show "Custom" and be disabled
      if (!voice) {
        this.assistantForm.properties.elevenlabs_voiceId = '';
      }
    },
    populateVoices() {
      const voices = speechSynthesis.getVoices();
      if (voices.length > 0) {
        console.log('Voices already loaded');
        this.speechApiVoices = voices;
      } else {
        console.log('Voices not loaded, waiting for onvoiceschanged');
        speechSynthesis.onvoiceschanged = () => {
          this.speechApiVoices = speechSynthesis.getVoices();
        };
      }
    },
  },
  async mounted() {
    try {
      const elevenLabsVoices = await this.textToSpeech.getElevenlabsVoices();
      this.elevenLabsVoices = elevenLabsVoices['voices'];
      console.log('this.elevenLabsVoices', this.elevenLabsVoices);
      this.updateVoiceIdSelection();

      this.elevenLabsModels = await this.textToSpeech.getElevenlabsModels();
      console.log('this.elevenLabsModels', this.elevenLabsModels);
      this.populateVoices();
    } catch (error) {
      this.appStore.handleError(error);
    }
  },
  unmounted() {
    speechSynthesis.onvoiceschanged = null;
  },
});
</script>

<template>
  <div class="mb-3 p-3">
    <label class="form-label my-2" for="selectTTSModel">Text-to-Speech</label>
    <select id="selectTTSModel" v-model="assistantForm.properties[PersonaProperties.TTS_TYPE]"
            aria-label="Text-to-Speech"
            class="form-select my-2">
      <option value="">disabled</option>
      <option value="speechAPI">Speech API</option>
      <option value="elevenlabs">Elevenlabs</option>
    </select>


    <div v-if="assistantForm.properties[PersonaProperties.TTS_TYPE] === 'elevenlabs'">
      <label class="form-label my-2" for="selectElevenLabsModel">Model</label>
      <select id="selectElevenLabsModel"
              v-model="assistantForm.properties[PersonaProperties.ELEVENLABS_MODELID]"
              aria-label="ElevenLabs Model"
              class="form-select my-2" @change="onChangeElevenLabsVoice">
        <option v-for="model in elevenLabsModels" :key="model.model_id" :value="model.model_id">{{
            model.name
          }}
        </option>
      </select>

      <label class="form-label my-2" for="selectElevenLabsVoice">Voice</label>
      <select id="selectElevenLabsVoice" ref="elevenLabsSelectedVoiceId"
              v-model="assistantForm.properties[PersonaProperties.ELEVENLABS_VOICEID]"
              aria-label="ElebenLabs Voice"
              class="form-select my-2">
        <option v-for="voice in elevenLabsVoices" :key="voice.voice_id" :value="voice.voice_id">{{
            voice.name
          }} - {{ voice.labels.description }} {{ voice.labels.age }} {{ voice.labels.accent }}
          {{ voice.labels.gender }}

        </option>
      </select>

      <label class="form-label" for="elevenlabsVoiceID">Voice-ID</label>
      <input id="elevenlabsVoiceID"
             v-model="assistantForm.properties[PersonaProperties.ELEVENLABS_VOICEID]"
             class="form-control"
             maxlength="32" required
             type="text">

      <label class="form-label my-2" for="rangeELVoiceSimBoost">Similarity boost</label>
      <div class="container">
        <div class="row">
          <div class="col-10 p-0">
            <input id="rangeELVoiceSimBoost"
                   v-model="assistantForm.properties[PersonaProperties.ELEVENLABS_SIMILARITYBOOST]"
                   class="form-range" max="1.0" min="0.0"
                   step="0.1" type="range">
          </div>
          <div class="col-2">
            <label>{{
                assistantForm.properties[PersonaProperties.ELEVENLABS_SIMILARITYBOOST]
              }}</label>
          </div>
        </div>
      </div>

      <label class="form-label my-2" for="rangeELVoiceStability">Stability</label>
      <div class="container">
        <div class="row">
          <div class="col-10 p-0">
            <input id="rangeELVoiceStability"
                   v-model="assistantForm.properties[PersonaProperties.ELEVENLABS_STABILITY]"
                   class="form-range"
                   max="1.0" min="0.0"
                   step="0.1" type="range">
          </div>
          <div class="col-2">
            <label>{{ assistantForm.properties[PersonaProperties.ELEVENLABS_STABILITY] }}</label>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="assistantForm.properties[PersonaProperties.TTS_TYPE] === TTSType.SPEECHAPI">
      <label class="form-label my-2" for="selectSpeechAPIVoice">Voices</label>
      <select id="selectSpeechAPIVoice"
              v-model="assistantForm.properties[PersonaProperties.SPEECHAPI_VOICE]"
              aria-label="SpeechAPI Voice"
              class="form-select my-2">
        <option v-for="voice in speechApiVoices" :key="voice.name" :value="voice.name">{{
            voice.name
          }}
        </option>
      </select>
    </div>

  </div>
</template>
