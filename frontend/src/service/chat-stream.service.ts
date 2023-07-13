import ChatMessage from '@/store/to/chat-message';
import Role from '@/store/to/role';
import {useChatStore} from '@/store/chat-store';
import ChatChoice from '@/store/to/chat-choice';
import axios from 'axios';
import hljs from 'highlight.js';

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

      if (this.hasJSONData(data)) {
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

    const processedMessage = await this.postProcessLastMessage(sessionId);
    processedMessage.content = this.replaceCodeContent(processedMessage.content);

    store.messages.pop();
    store.messages.push(processedMessage);

  }

  replaceCodeContent(originalString: string) {
    // Use a regular expression to find the code tag and its content.
    const regex = /(<code class="(.*)">)([\s\S]*?)(<\/code>)/g;

    let newContent = originalString;
    let match;
    while ((match = regex.exec(originalString)) !== null) {
      const lang = match[2];
      const hljsLang = hljs.getLanguage(lang);
      let highlighted = '';
      if (hljsLang) {
        highlighted = hljs.highlight(match[2], match[3]).value;
      } else {
        highlighted = hljs.highlightAuto(match[3]).value;
      }

      newContent = newContent.replace(match[0], `${match[1]}${highlighted}${match[4]}`);
    }

    return newContent;
  }

  hasJSONData(data: string): boolean {
    return data.indexOf('{') != -1;
  }

  async postProcessLastMessage(sessionId: string): Promise<ChatMessage> {
    try {
      const result = await axios.get(`/api/v1/chat/session/${sessionId}/postprocess/last`);
      return result.data;
    } catch (error) {
      throw new Error('Error reading session entry:  ' + error);
    }
  }

  parseStreamResponse(data: string): ChatChoice | undefined {
    console.log('DATA TO PARSE: ', data);
    const startIndexJson = data.indexOf('{');
    try {
      const json = data.substring(startIndexJson);
      const choice = JSON.parse(json);

      if (choice.delta && choice.delta.content) {
        const content = choice.delta.content;
        choice.delta.content = content.replaceAll('\n\n', '<p/>').replaceAll('\n', '<br/>');
      }

      console.log('CHOICE: ', choice);
      return choice;
    } catch (err) {
      console.log('Cannot parse JSON in Message.', err);
      return undefined;
    }
  }

}

export default ChatStreamService;

