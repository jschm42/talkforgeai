import ChatMessage from './chat-message';

class ChatSession {
  sessionId = '';
  messages: Array<ChatMessage> = [];
  persona = null;

}

export default ChatSession;
