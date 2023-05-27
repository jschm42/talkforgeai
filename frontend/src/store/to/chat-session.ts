import ChatMessage from './chat-message';
import IdentityUtil from '../../util/identity-util';
import Persona, {DEFAULT_PERSONA} from './persona';

class ChatSession {
  sessionId: string;
  systemMessages: Array<ChatMessage> = [];
  messages: Array<ChatMessage> = [];
  processedMessages: Array<ChatMessage> = [];
  persona: Persona;

  constructor() {
    this.sessionId = IdentityUtil.generateUUID();
    this.systemMessages = [];
    this.messages = [];
    this.processedMessages = [];
    this.persona = DEFAULT_PERSONA;
  }

}

export default ChatSession;
