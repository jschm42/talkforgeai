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
}

export default ChatService;
