import ChatMessage from "../service/to/chat-message";
import Role from "../service/to/role";
import OpenAiRenderer from "./openai.renderer";
import ChatSession from "../service/to/chat-session";

const regex = /"delta":\s*{"(role|content)":"([^"]+)"/;

class ChatRenderer {
  async submit(prompt: string, session: ChatSession) {
    // @ts-ignore
    const config = window.configAPI.getConfig();
    const openAiSerice = new OpenAiRenderer();
    const chatRenderer = new ChatRenderer();

    const previousMessages = chatRenderer.getPreviousMessages(session.system, session.messages,
      session.processedMessages);

    const userMessage = new ChatMessage(Role.USER, prompt);
    session.messages.push(userMessage);

    const submitMessages = [...previousMessages, userMessage];

    // @ts-ignore
    const response = await openAiSerice.chatCompletion(submitMessages, true);
    const reader = response.body.getReader();

    const processedMessage = new ChatMessage(Role.ASSISTANT, '');
    session.processedMessages.push(processedMessage);

    // @ts-ignore
    let done = false;
    let command = '';
    let commandMode = false;

    while (!done) {
      const row = await reader.read();
      const str = new TextDecoder().decode(row.value);
      const parsed = this.parseStreamResponse(str);

      const contentArray = parsed.filter(e => e.type === 'content').map(e => e.value);

      for (let value of contentArray) {
        console.log('COMMAND MODE', commandMode);
        if (value) {

          if (value === '``') {
            if (commandMode) {
              console.log('TURN COMMAND MODE OFF');
              commandMode = false;
              console.log('COMMAND FOUND: ', command);
            } else {
              console.log('TURN COMMAND MODE ON');
              commandMode = true;
              command = '';
            }
          }

          if (commandMode) {
            command += value;
            console.log('CUR COMMAND VALUE', command);
          } else {
            value = value.replace('\\n\\n', '<p/>');

            console.log('CUR VALUE', value);
            this.addToLastMessage(value, session);
          }

        }
      }

      done = row.done;
    }
    console.log('Stream complete');
  }

  addToLastMessage(content: string, session: ChatSession) {
    const lastMessage = session.processedMessages[session.processedMessages.length - 1];
    lastMessage.content += content;
  }

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
