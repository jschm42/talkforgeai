import axios from 'axios';

class ChatService {
  async readSessionEntries() {
    try {
      return await axios.get('/api/v1/chat/session');
    } catch (error) {
      console.error('Error reading index entries: ', error);
    }
    return [];
  }
}

export default ChatService;
