import ChatMessage from "../service/to/chat-message";
import Role from "../service/to/role";

const regex = /"delta":\s*{"(role|content)":"([^"]+)"/;

class ChatRenderer {
  getPreviousMessages(system: string, messages: Array<ChatMessage>, processedMessages: Array<ChatMessage>) {
    const previousMessages = [];
    previousMessages.push(new ChatMessage(Role.SYSTEM, system));

    for (let i = 0; i < messages.length; i++) {
      if (messages[i].role === Role.USER) {
        previousMessages.push(Object.assign({}, processedMessages[i]));
      } else {
        previousMessages.push(Object.assign({}, messages[i]));
      }
    }
    return previousMessages;
  }

  parseStreamResponse(str: string) {
    return str.split("\n\n")
      .filter(e => e.length > 0)
      .map(e => regex.exec(e))
      .map(p => {
        if (p === null) return {}
        return {
          type: p[1], value: p[2]
        }
      });
  }
}

export default ChatRenderer;
