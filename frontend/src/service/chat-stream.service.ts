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

  async sleep(delay: number) {
    return new Promise((resolve) => setTimeout(resolve, delay));
  }

  async streamSubmit(sessionId: string, content: string, chunkUpdateCallback: () => void) {

    const store = useChatStore();
    const isFunctionCall = false;

    const newMessage = new ChatMessage(Role.ASSISTANT, '');
    store.messages.push(newMessage);
    store.updateStatus('Thinking...', 'running');

    return new Promise((resolve, reject): void => {
      const evtSource = new EventSource('/api/v1/chat/stream/submit?sessionId=' + sessionId + '&content=' + content);

      evtSource.addEventListener('complete', (event) => {
        store.removeStatus();
        evtSource.close();
        this.postStreamProcessing(store, sessionId, isFunctionCall).then();
        resolve(event);
      });

      evtSource.onmessage = (event) => {
        console.log('Source Message', event);
        this.processData(event.data, store, chunkUpdateCallback);
      };

      evtSource.onerror = (event) => {
        console.log('onError: ', event);
        evtSource.close();
        store.updateStatus('Error while streaming.', 'error');
        resolve(event);
      };

    });
  }

  async postStreamProcessing(store: any, sessionId: string, isFunctionCall: boolean) {
    if (!isFunctionCall) {
      const processedMessage = await this.postProcessLastMessage(sessionId);
      processedMessage.content = highlightingService.replaceCodeContent(processedMessage.content);

      store.messages.pop();
      store.messages.push(processedMessage);
    }
  }

  processData(data: string, store: any, chunkUpdateCallback: () => void) {

    if (this.hasJSONData(data)) {
      const chatChoice = this.parseStreamResponse(data);
      //console.log('PARSED STREAM DATA: ', chatChoice);
      const lastMessage = store.messages[store.messages.length - 1];

      if (chatChoice?.delta) {
        if (chatChoice.delta.content) {
          //console.log('UPDATING MESSAGE', chatChoice?.delta.content);
          lastMessage.content += chatChoice?.delta.content;
          //await this.sleep(500);
          chunkUpdateCallback();
        } else if (chatChoice.delta.function_call && chatChoice.delta.function_call.arguments) {
          console.log('FUNCTION CALL', chatChoice.delta.function_call);
          // isFunctionCall = true;

          if (!lastMessage.function_call) {
            lastMessage.function_call = new FunctionCall();
          }

          lastMessage.function_call.name = chatChoice?.delta.function_call.name;
          lastMessage.function_call.arguments += chatChoice?.delta.function_call.arguments;
        }
      }

    }
  }

  hasJSONData(data: string): boolean {
    return data.indexOf('{') != -1;
  }

  async postProcessLastMessage(sessionId: string): Promise<ChatMessage> {
    try {
      const result = await axios.get(`/api/v1/session/${sessionId}/postprocess/last`);
      return result.data;
    } catch (error) {
      throw new Error('Error reading session entry:  ' + error);
    }
  }

  parseStreamResponse(data: string): ChatChoice | undefined {
    //console.log('DATA TO PARSE: ', data);
    //const startIndexJson = data.indexOf('{');
    try {
      //const json = data.substring(startIndexJson);
      const choice = JSON.parse(data);

      if (choice.delta && choice.delta.content) {
        const content = choice.delta.content;
        choice.delta.content = content.replaceAll('\n\n', '<p/>').replaceAll('\n', '<br/>');
      }

      //console.log('CHOICE: ', choice);
      return choice;
    } catch (err) {
      console.log('Cannot parse JSON in Message.', err);
      return undefined;
    }
  }

}

export default ChatStreamService;

