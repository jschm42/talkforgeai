import Role from './role';

class ChatMessage {

  role: Role;

  content: string;

  constructor(role: Role, content: string) {
    this.role = role;
    this.content = content;
  }
}

export default ChatMessage;
