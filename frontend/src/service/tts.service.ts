import axios from 'axios';
import Persona from '@/store/to/persona';

class TtsService {

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

  }
}

export default TtsService;
