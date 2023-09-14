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

  buffer = '';

  async sleep(delay: number) {
    return new Promise((resolve) => setTimeout(resolve, delay));
  }

  async streamSubmit(sessionId: string, content: string, chunkUpdateCallback: () => void) {

    const store = useChatStore();
    const isFunctionCall = false;

    const newMessage = new ChatMessage(Role.ASSISTANT, '');
    store.messages.push(newMessage);
    store.updateStatus('Thinking...', 'running');

    const response = await this.fetchSSE(content, sessionId);
    const reader = response.body?.getReader();

    // Iterate over stream
    if (reader) {
      const decoder = new TextDecoder('utf-8');
      let partial = '';
      while (true) { // eslint-disable-line no-constant-condition
        const {done, value} = await reader.read();
        if (done) {
          console.log("STREAM DONE");
          await this.postStreamProcessing(store, sessionId, isFunctionCall);
          store.removeStatus();
          break;
        }
        if (value) {
          const chunk = decoder.decode(value, {stream: true});
          //console.log("CHUNK", chunk);
          partial += chunk;
          const parts = partial.split('\n');
          partial = parts.pop() || '';
          this.buffer = '';
          for (const part of parts) {
            if (part && part.startsWith("data:")) {
                const data = part.substring(5);
                this.processData(data, store, chunkUpdateCallback);
                await this.sleep(100);
            }
          }
        }
      }
    }
  }

   escapeHtml(html: string) {
    // // Create a temporary element to encode the HTML
    // const temp = document.createElement('div');
    // temp.textContent = html;
    //
    // // Get the encoded HTML as plain text
    // return temp.innerHTML;
     console.log("VOR REPLACE", html);
     const t =  html.replace(/</g, '&lt;').replace(/>/g, '&gt;');
     console.log("NACH REPLACE", t);
      return t;
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
      const chatChoice = JSON.parse(data);
      const lastMessage = store.messages[store.messages.length - 1];

      if (chatChoice?.delta) {
        if (chatChoice.delta.content) {
          //console.log('UPDATING MESSAGE', chatChoice?.delta.content);
          let newContent = chatChoice?.delta.content;
          newContent = this.escapeHtml(newContent)
          newContent = newContent.replaceAll(/\n/g, '<br/>');
          lastMessage.content += newContent;
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

  async fetchSSE(content: string, sessionId: string): Promise<Response> {
    return await fetch('/api/v1/chat/stream/submit', {
      method: 'POST',
      cache: 'no-cache',
      keepalive: true,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'text/event-stream',
      },
      body: JSON.stringify({
        content,
        sessionId,
      }),
    });
  }

}

export default ChatStreamService;

