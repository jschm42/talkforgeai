import ChatMessage from '@/store/to/chat-message';
import Role from '@/store/to/role';
import {useChatStore} from '@/store/chat-store';
import ChatChoice from '@/store/to/chat-choice';

const STREAM_REGEX = /{"content":(.*?)},/;
const QUOTE_REGEX = /\\"/;

class ChatStreamService {

  async streamSubmit(
    sessionId: string, content: string, messageCallback: (content: string[], isDone: boolean) => void) {

    const store = useChatStore();

    const response = await fetch('http://localhost:8090/api/v1/chat/stream/submit', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },

      //make sure to serialize your JSON body
      body: JSON.stringify({sessionId, content}),
    });

    if (!response.body) {
      console.error('ReadableStream not yet supported in this browser.');
      return;
    }

    const reader = response.body.getReader();

    const decoder = new TextDecoder();
    let done = false;

    const newMessage = new ChatMessage(Role.ASSISTANT, '');
    store.messages.push(newMessage);

    while (!done) {
      const row = await reader.read();

      const data = decoder.decode(row.value, {stream: true});
      console.log('DATA: ', data);

      if (this.hasJSONDate(data)) {
        const chatChoice = this.parseStreamResponse(data);
        console.log('PARSED STREAM DATA: ', chatChoice);
        const lastMessage = store.messages[store.messages.length - 1];
        if (chatChoice?.delta && chatChoice.delta.content) {
          lastMessage.content += chatChoice?.delta.content;
        }
      }

      done = row.done;

      //messageCallback(content, done);
    }

    console.log('Stream complete');

  }

  hasJSONDate(data: string): boolean {
    return data.indexOf('{') != -1;
  }

  parseStreamResponse(data: string): ChatChoice | undefined {
    console.log('DATA TO PARSE: ', data);
    const startIndexJson = data.indexOf('{');
    try {
      const json = JSON.parse(data.substring(startIndexJson));
      console.log('JSON: ', json);
      return json;
    } catch (err) {
      console.log('Cannot parse JSON in Message.', err);
      return undefined;
    }
  }

}

export default ChatStreamService;

