<script>
import {defineComponent} from 'vue';
import {storeToRefs} from 'pinia';
import {usePersonaFormStore} from '@/store/persona-form-store';

export default defineComponent({
  name: 'PersonaTabVoice',
  data() {
    return {
      speechAPIVoices: [],
    };
  },
  setup() {
    const {personaForm} = storeToRefs(usePersonaFormStore());

    return {personaForm};
  },
  methods: {
    populateVoiceList() {
      if (typeof speechSynthesis === 'undefined') {
        return;
      }

      const voices = speechSynthesis.getVoices();
      console.log('Voices:', voices);
      this.speechAPIVoices = voices;
      this.personaForm.properties.speechAPI_voice = voices[0].name;
    },

  },
  mounted() {
    console.log('MOunted');

    if (typeof speechSynthesis !== 'undefined' && speechSynthesis.onvoiceschanged !== undefined) {
      console.log('SpeechSynthesis supported');
      speechSynthesis.onvoiceschanged = this.populateVoiceList;
    }
  },
  unmounted() {
    console.log('Unmounted');
    speechSynthesis.onvoiceschanged = null;
  },
});
</script>

<template>
  <div class="mb-3 p-3">
    <label class="form-label my-2" for="selectTTSModel">Text-to-speech model</label>
    <select id="selectTTSModel" v-model="personaForm.properties.voice_type" aria-label="Text-to-speech model"
            class="form-select my-2">
      <option value="">Speech API</option>
      <option value="elevenlabs">Elevenlabs</option>
    </select>


    <div v-if="personaForm.properties.voice_type === 'elevenlabs'">
      <label class="form-label" for="elevenlabsVoiceID">Voice-ID</label>
      <input id="elevenlabsVoiceID" v-model="personaForm.properties.elevenlabs_voiceId" class="form-control"
             maxlength="32" required
             type="text">

      <label class="form-label my-2" for="rangeELVoiceSimBoost">Similarity boost</label>
      <div class="container">
        <div class="row">
          <div class="col-10 p-0">
            <input id="rangeELVoiceSimBoost" v-model="personaForm.properties.elevenlabs_similarityBoost"
                   class="form-range" max="1.0" min="0.0"
                   step="0.1" type="range">
          </div>
          <div class="col-2">
            <label>{{ personaForm.properties.elevenlabs_similarityBoost }}</label>
          </div>
        </div>
      </div>

      <label class="form-label my-2" for="rangeELVoiceStability">Stability</label>
      <div class="container">
        <div class="row">
          <div class="col-10 p-0">
            <input id="rangeELVoiceStability" v-model="personaForm.properties.elevenlabs_stability" class="form-range"
                   max="1.0" min="0.0"
                   step="0.1" type="range">
          </div>
          <div class="col-2">
            <label>{{ personaForm.properties.elevenlabs_stability }}</label>
          </div>
        </div>
      </div>
    </div>

    <div v-else>
      <label class="form-label my-2" for="selectSpeechAPIVoice">Voices</label>
      <select id="selectSpeechAPIVoice" v-model="personaForm.properties.speechAPI_voice" aria-label="SpeechAPI Voice"
              class="form-select my-2">
        <option v-for="voice in speechAPIVoices" :key="voice.name" :value="voice.name">{{ voice.name }}</option>
      </select>
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
