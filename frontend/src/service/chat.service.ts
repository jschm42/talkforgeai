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

  async createNewSession() {
    try {
      const result = await axios.post('/api/v1/chat/create', {});
      return result.data;
    } catch (error) {
      console.error('Error creating chat session: ', error);
    }
    return null;
  }

  async submit(sessionId: string, prompt: string) {
    try {
      const result = await axios.post(`/api/v1/chat/submit/${sessionId}`, {prompt});
      return result.data;
    } catch (error) {
      console.error('Error submitting prompt: ', error);
    }
    return null;
  }
}

export default ChatService;
