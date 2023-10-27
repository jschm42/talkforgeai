import axios from 'axios';
import Persona from '@/store/to/persona';
import PersonaProperties from '@/service/persona.properties';

class TtsService {

  async getElevenlabsVoices() {
    try {
      const result = await axios.get(
        `/api/v1/tts/voices`,
        {
          timeout: 5000,
          headers: {
            'Content-Type': 'application/json',
          },
        });
      return result.data;
    } catch (error) {
      console.error('Error submitting prompt: ', error);
    }
  }

  async getElevenlabsModels() {
    try {
      const result = await axios.get(
        `/api/v1/tts/models`,
        {
          timeout: 5000,
          headers: {
            'Content-Type': 'application/json',
          },
        });
      return result.data;
    } catch (error) {
      console.error('Error submitting prompt: ', error);
    }
  }

  async speakElevenlabs(text: string, persona: Persona) {
    try {
      const result = await axios.post(
        `/api/v1/tts/stream`,
        {text, personaId: persona.personaId},
        {
          timeout: 50000,
          headers: {
            'Content-Type': 'application/json',
          },
          responseType: 'blob',
        });
      return result.data;
    } catch (error) {
      console.error('Error submitting prompt: ', error);
    }
    return null;
  }

  async speakSpeechAPI(text: string, persona: Persona) {
    return new Promise((resolve, reject) => {
      const msg = new SpeechSynthesisUtterance();
      msg.text = text;
      const personaVoice = persona.properties[PersonaProperties.SPEECHAPI_VOICE];
      const voice = window.speechSynthesis.getVoices().find((voice) => voice.name === personaVoice);

      if (voice) {
        console.log('Using voice: ', voice);
        msg.voice = voice;
      }

      msg.onend = function(e) {
        resolve(true);
      };

      window.speechSynthesis.speak(msg);
    });
  }
}

export default TtsService;
