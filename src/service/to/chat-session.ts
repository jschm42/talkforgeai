import ChatMessage from './chat-message';

class ChatSession {
  sessionId: string = '';
  messages: Array<ChatMessage> = [];
  processedMessages: Array<ChatMessage> = [];
}

export default ChatSession;
