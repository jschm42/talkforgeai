import axios from 'axios';
import Session from '@/store/to/session';

class ChatService {
  async readSessionEntries(): Promise<Array<Session>> {
    try {
      return await axios.get('/api/v1/chat/session', {
        // headers: {
        //     'Access-Control-Allow-Origin': 'http://localhost:8090',
        // },
      });
    } catch (error) {
      console.error('Error reading index entries: ', error);
    }
    return [];
  }
}

export default ChatService;
