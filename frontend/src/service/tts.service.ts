import axios from 'axios';

class TtsService {
  async speak(text: string, personaId: string) {
    try {
      const result = await axios.post(
        `/api/v1/tts/stream`,
        {text, personaId},
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
}

export default TtsService;
