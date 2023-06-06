import axios from 'axios';
import Session from '@/store/to/session';

class ChatService {
  async readSessionEntries(): Promise<Array<Session>> {
    try {
      const result = await axios.get('/api/v1/chat/session');
      return result.data;
    } catch (error) {
      console.error('Error reading index entries: ', error);
    }
    return [];
  }

  async createNewSession(personaId: string) {
    console.log('Creating new session with personaId:', personaId);
    try {
      const result = await axios.post('/api/v1/chat/create', {personaId});
      return result.data;
    } catch (error) {
      console.error('Error creating chat session: ', error);
    }
    return null;
  }

  async getLastResult(sessionId: string) {
    try {
      const result = await axios.get(
        `/api/v1/chat/result/${sessionId}`,
        {
          timeout: 50000,
          headers: {
            'Content-Type': 'application/json',
          },
        });
      return result.data;
    } catch (error) {
      console.error('Error submitting prompt: ', error);
    }
    return null;
  }

  async submit(sessionId: string, prompt: string) {
    try {
      const result = await axios.post(
        `/api/v1/chat/submit`,
        {sessionId, prompt},
        {
          timeout: 50000,
          headers: {
            'Content-Type': 'application/json',
          },
        });
      return result.data;
    } catch (error) {
      console.error('Error submitting prompt: ', error);
    }
    return null;
  }
}

export default ChatService;
