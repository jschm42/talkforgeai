import Role from './role';

class FunctionCall {
  name: string | undefined;
  arguments: string | undefined;
}

class ChatMessage {

  role: Role;

  content: string;

  name: string | undefined;

  function_call: FunctionCall | undefined;

  constructor(role: Role, content: string, name?: string, function_call?: FunctionCall) {
    this.role = role;
    this.content = content;
    this.name = name;
    this.function_call = function_call;
  }
}

export default ChatMessage;
export {FunctionCall};
