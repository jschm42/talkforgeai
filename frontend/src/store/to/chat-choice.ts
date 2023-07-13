import ChatMessage from '@/store/to/chat-message';

class ChatChoice {
  index: number;
  delta: ChatMessage;
  finish_reason: string;

  constructor(index: number, delta: ChatMessage, finish_reason: string) {
    this.index = index;
    this.delta = delta;
    this.finish_reason = finish_reason;
  }
}

export default ChatChoice;
