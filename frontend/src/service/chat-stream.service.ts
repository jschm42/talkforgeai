import ChatMessage, {FunctionCall} from '@/store/to/chat-message';
import Role from '@/store/to/role';
import {useChatStore} from '@/store/chat-store';
import ChatChoice from '@/store/to/chat-choice';
import axios from 'axios';
import HighlightingService from '@/service/highlighting.service';

const STREAM_REGEX = /{"content":(.*?)},/;
const QUOTE_REGEX = /\\"/;

const highlightingService = new HighlightingService();

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

    let isFunctionCall = false;
    while (!done) {
      const row = await reader.read();

      const data = decoder.decode(row.value, {stream: true});
      console.log('DATA: ', data);

      if (this.hasJSONData(data)) {
        const chatChoice = this.parseStreamResponse(data);
        console.log('PARSED STREAM DATA: ', chatChoice);
        const lastMessage = store.messages[store.messages.length - 1];

        if (chatChoice?.delta) {
          if (chatChoice.delta.content) {
            lastMessage.content += chatChoice?.delta.content;
          } else if (chatChoice.delta.function_call && chatChoice.delta.function_call.arguments) {
            console.log('FUNCTION CALL', chatChoice.delta.function_call);
            isFunctionCall = true;

            if (!lastMessage.function_call) {
              lastMessage.function_call = new FunctionCall();
            }

            lastMessage.function_call.name = chatChoice?.delta.function_call.name;
            lastMessage.function_call.arguments += chatChoice?.delta.function_call.arguments;
          }
        }

      }

      done = row.done;

      //messageCallback(content, done);
    }

    console.log('Stream complete');

    if (!isFunctionCall) {
      const processedMessage = await this.postProcessLastMessage(sessionId);
      processedMessage.content = highlightingService.replaceCodeContent(processedMessage.content);

      store.messages.pop();
      store.messages.push(processedMessage);
    }
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

