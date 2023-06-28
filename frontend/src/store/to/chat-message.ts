import Role from './role';

class ChatMessage {

  role: Role;

  content: string;

  name: string | undefined;

  function_call: undefined;

  constructor(role: Role, content: string, name?: string, function_call?: undefined) {
    this.role = role;
    this.content = content;
    this.name = name;
    this.function_call = function_call;
  }
}

export default ChatMessage;
