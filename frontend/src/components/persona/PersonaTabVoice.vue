<script>
import {defineComponent} from 'vue';
import {storeToRefs} from 'pinia';
import {usePersonaFormStore} from '@/store/persona-form-store';

export default defineComponent({
  name: 'PersonaTabVoice',
  data() {
    return {
      ttsModel: 'speechAPI',
      speechAPIVoices: [],
      speechAPIVoiceSelected: '',
      elevenlabsVoiceID: '',
      elevenlabsSimilarityBoost: 0.5,
      elevenlabsStability: 0.5,
    };
  },
  setup() {
    const {personaForm} = storeToRefs(usePersonaFormStore());

    return {personaForm};
  },
  methods: {
    onSelectTTSModelChange(event) {
      // Change ttsModel to the selected value
      this.ttsModel = event.target.value;
    },
  },
  mounted() {
    document.addEventListener('DOMContentLoaded', () => {
      if (speechSynthesis.onvoiceschanged !== undefined) {
        speechSynthesis.onvoiceschanged = () => {
          const voices = speechSynthesis.getVoices();
          console.log('Voices:', voices);
          this.speechAPIVoices = voices;
        };
      }
    });
  },
});
</script>

<template>
  <div class="mb-3 p-3">
    <label class="form-label my-2" for="selectTTSModel">Text-to-speech model</label>
    <select id="selectTTSModel" aria-label="Text-to-speech model" class="form-select my-2"
            @change="onSelectTTSModelChange">
      <option selected value="speechAPI">Speech API</option>
      <option value="elevenlabs">Elevenlabs</option>
    </select>

    <div v-if="ttsModel === 'speechAPI'">
      <label class="form-label my-2" for="selectSpeechAPIVoice">Voices</label>
      <select id="selectSpeechAPIVoice" v-model="speechAPIVoiceSelected" aria-label="SpeechAPI Voice"
              class="form-select my-2">
        <option v-for="voice in speechAPIVoices" :key="voice.name" :value="voice.name">{{ voice.name }}</option>
      </select>
    </div>
    <div v-else>
      <label class="form-label" for="elevenlabsVoiceID">Voice-ID</label>
      <input id="elevenlabsVoiceID" v-model="elevenlabsVoiceID" class="form-control" maxlength="32" required
             type="text">

      <label class="form-label my-2" for="rangeELVoiceSimBoost">Similarity boost</label>
      <div class="container">
        <div class="row">
          <div class="col-10 p-0">
            <input id="rangeELVoiceSimBoost" v-model="elevenlabsSimilarityBoost" class="form-range" max="1.0" min="0.0"
                   step="0.1" type="range">
          </div>
          <div class="col-2">
            <label>{{ elevenlabsSimilarityBoost }}</label>
          </div>
        </div>
      </div>

      <label class="form-label my-2" for="rangeELVoiceStability">Stability</label>
      <div class="container">
        <div class="row">
          <div class="col-10 p-0">
            <input id="rangeELVoiceStability" v-model="elevenlabsStability" class="form-range" max="1.0" min="0.0"
                   step="0.1" type="range">
          </div>
          <div class="col-2">
            <label>{{ elevenlabsStability }}</label>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>
.placeholder-image {
  font-size: 7em;
  width: 200px;
  height: 200px;
}

.thumbnail-image {
  width: 200px;
  height: 200px;
}

</style>
