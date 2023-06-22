import Role from './role';

class ChatMessage {

  role: Role;

  content: string;

  name: string | undefined;

  functionCall: undefined;

  constructor(role: Role, content: string, name?: string, functionCall?: undefined) {
    this.role = role;
    this.content = content;
    this.name = name;
    this.functionCall = functionCall;
  }
}

export default ChatMessage;
