import axios from 'axios';
import Session from '@/store/to/session';

class ChatService {
  async readSessionEntries(personaId: string): Promise<Array<Session>> {
    try {
      const result = await axios.get(`/api/v1/session/${personaId}/sessions`);
      return result.data;
    } catch (error) {
      throw new Error('Error reading index entries: ' + error);
    }
  }

  async readSessionEntry(sessionId: string): Promise<Session> {
    try {
      const result = await axios.get(`/api/v1/session/${sessionId}`);
      return result.data;
    } catch (error) {
      throw new Error('Error reading session entry:  ' + error);
    }
  }

  async createNewSession(personaId: string) {
    console.log('Creating new session with personaId:', personaId);
    try {
      const result = await axios.post('/api/v1/session/create', {personaId});
      return result.data;
    } catch (error) {
      throw new Error('Error creating chat session: ' + error);
    }
  }

  async deleteSession(sessionId: string) {
    console.log(`Deleting session ${sessionId}.`);

    try {
      const result = await axios.delete(
        `/api/v1/session/${sessionId}`,
        {
          timeout: 50000,
          headers: {
            'Content-Type': 'application/json',
          },
        });
      return result.data;
    } catch (error) {
      throw new Error('Error reading session entry:  ' + error);
    }
  }

  async updateSessionTitle(sessionId: string, newTitle: string) {
    console.log(`Updating title of session ${sessionId} to ${newTitle}.`);

    try {
      const result = await axios.post(
        `/api/v1/session/${sessionId}/title`,
        {newTitle},
        {
          timeout: 50000,
          headers: {
            'Content-Type': 'application/json',
          },
        });
      return result.data;
    } catch (error) {
      throw new Error('Error reading session entry:  ' + error);
    }
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
      throw new Error('Error submitting prompt: ' + error);
    }
  }

  async submit(sessionId: string, content: string) {
    try {
      const result = await axios.post(
        `/api/v1/chat/submit`,
        {sessionId, content},
        {
          timeout: 50000,
          headers: {
            'Content-Type': 'application/json',
          },
        });
      return result.data;
    } catch (error) {
      throw new Error('Error submitting prompt: ' + error);
    }
  }

  async submitFunctionConfirm(sessionId: string) {
    try {
      const result = await axios.post(
        `/api/v1/chat/submit/function/confirm/${sessionId}`,
        {sessionId},
        {
          timeout: 50000,
          headers: {
            'Content-Type': 'application/json',
          },
        });
      return result.data;
    } catch (error) {
      throw new Error('Error submitting prompt: ' + error);
    }
  }
}

export default ChatService;
