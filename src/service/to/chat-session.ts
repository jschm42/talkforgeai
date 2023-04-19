import ChatMessage from './chat-message';
import IdentityUtil from '../../util/identity-util';
import {DEFAULT_PERSONA} from './persona';

class ChatSession {
  sessionId: string;
  messages: Array<ChatMessage> = [];
  processedMessages: Array<ChatMessage> = [];
  personaName: string;
  system: string;

  constructor() {
    this.sessionId = IdentityUtil.generateUUID();
    this.messages = [];
    this.processedMessages = [];
    this.personaName = DEFAULT_PERSONA.name;
    this.system = DEFAULT_PERSONA.system;
  }

}

export default ChatSession;
