import ChatMessage from './chat-message';

class ChatSession {
  sessionId = null;
  systemMessages: Array<ChatMessage> = [];
  messages: Array<ChatMessage> = [];
  processedMessages: Array<ChatMessage> = [];
  persona = null;

  constructor() {
    this.systemMessages = [];
    this.messages = [];
    this.processedMessages = [];
  }

}

export default ChatSession;
