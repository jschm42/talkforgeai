import ChatMessage from '../service/to/chat-message';
import Role from '../service/to/role';
import OpenAiRenderer from './openai.renderer';
import ChatSession from '../service/to/chat-session';
import {toRaw} from 'vue';
import hljs from 'highlight.js';

const regex = /"delta":\s*{"(role|content)":"([^"]+)"/;

class ChatRenderer {
  async submit(prompt: string, session: ChatSession) {
    // @ts-ignore
    const openAiSerice = new OpenAiRenderer();
    const chatRenderer = new ChatRenderer();

    const previousMessages = chatRenderer.getPreviousMessages(session.system, session.messages,
      session.processedMessages);

    const userMessage = new ChatMessage(Role.USER, prompt);
    session.messages.push(userMessage);
    session.processedMessages.push(userMessage);

    const submitMessages = [...previousMessages, userMessage];

    // @ts-ignore
    const response = await openAiSerice.chatCompletion(submitMessages, true);
    const reader = response.body.getReader();

    const processedMessage = new ChatMessage(Role.ASSISTANT, '');
    session.processedMessages.push(processedMessage);
    const message = new ChatMessage(Role.ASSISTANT, '');
    session.messages.push(message);

    // @ts-ignore
    let done = false;
    let command = '';
    let commandMode = false;
    let wordMode = false;
    let word = '';

    let messageContent = '';

    let regExCommandStart = /```[a-z]*\\n$/;

    /**
     * Variants:
     * ```java\n - Start of code block with language type
     * ```\n - Start of code block without language type
     *
     * ```\n\n - End of code block with language type
     */

    while (!done) {
      const row = await reader.read();
      const str = new TextDecoder().decode(row.value);
      const parsed = this.parseStreamResponse(str);

      const contentArray = parsed.filter(e => e.type === 'content').map(e => e.value);

      for (let value of contentArray) {
        console.log('COMMAND MODE', commandMode);
        if (value) {
          messageContent += value;

          console.log('CUR VALUE', value);

          if (commandMode) {
            command += value;
            this.appendOrReplaceTagInMessage(value, command, '<pre>', '</pre>', session);
          } else if (wordMode) {
            word += value;
            this.appendOrReplaceTagInMessage(value, word, `<code>`, '</code>', session);
          } else {
            this.addToLastMessage(value, session);
          }

          if (messageContent.match(regExCommandStart)) {
            if (commandMode) {
              console.log('TURN COMMAND MODE OFF');
              commandMode = false;
              //command = command.replace('`', '');
              //console.log('COMMAND FOUND: ', command);
            } else {
              console.log('TURN COMMAND MODE ON');
              commandMode = true;
              command = '';
            }
            /*
          } else if (value.endsWith('`')) {
            if (wordMode) {
              console.log('TURN WORD MODE OFF');
              wordMode = false;
              word = '';
            } else {
              console.log('TURN WORD MODE ON');
              wordMode = true;
            }

             */
          }

        }
      }

      done = row.done;

    }
    console.log('Stream complete');

    session.messages.push(new ChatMessage(Role.ASSISTANT, messageContent));

    // @ts-ignore
    //const transformed = await window.chatAPI.process(processedMessage);
    //this.updateLastProcessedMessage(transformed.content, session);
  }

  getLastMessage(session: ChatSession) {
    return toRaw(session.messages[session.messages.length - 1]);
  }

  addToLastMessage(content: string, session: ChatSession) {
    content = content.replace(/\\n\\n/g, '\n\n');
    content = content.replace(/\\n/g, '\n');
    content = content.replace(/\\/g, '\"');
    const lastProcessedMessage = session.processedMessages[session.processedMessages.length - 1];
    lastProcessedMessage.content += content;

    const lastMessage = session.messages[session.messages.length - 1];
    lastMessage.content += content;
  }

  highlightLastTagInMessage(startTag: string, endTag: string, session: ChatSession) {
    const lastProcessedMessage = session.processedMessages[session.processedMessages.length - 1];
    // Find the <pre> tag and extract its contents
    const preTagStart = lastProcessedMessage.content.lastIndexOf(startTag);
    const preTagEnd = lastProcessedMessage.content.lastIndexOf(endTag);
    const preTagContent = lastProcessedMessage.content.slice(preTagStart + 5, preTagEnd);

    // Append the new text to the <pre> contents
    const newPreTagContent = hljs.highlightAuto(preTagContent).value;
    // Replace the old <pre> contents with the new one
    lastProcessedMessage.content = lastProcessedMessage.content.slice(0, preTagStart + 5) + newPreTagContent +
      lastProcessedMessage.content.slice(preTagEnd);
  }

  appendOrReplaceTagInMessage(value: string, buffer: string, startTag: string, endTag: string, session: ChatSession) {
    buffer = buffer.replace(/\\n\\n/g, '\n\n');
    buffer = buffer.replace(/\\n/g, '\n');
    buffer = buffer.replace(/`/g, '');
    buffer = buffer.replace(/\\/g, '\"');

    const lastProcessedMessage = session.processedMessages[session.processedMessages.length - 1];

    if (lastProcessedMessage.content.endsWith(endTag)) {
      // Find the <pre> tag and extract its contents
      //console.log('LAST PROCESSED MSG', lastProcessedMessage.content);
      const preTagStart = lastProcessedMessage.content.lastIndexOf(startTag);
      const preTagEnd = lastProcessedMessage.content.lastIndexOf(endTag);
      //const preTagContent = lastProcessedMessage.content.slice(preTagStart + 5, preTagEnd);

      // Append the new text to the <pre> contents
      //let newPreTagContent = preTagContent + value;

      //console.log('BUFFER', buffer);
      const processed = hljs.highlightAuto(buffer).value;
      //console.log('PROCESSED', processed);

      // Replace the old <pre> contents with the new one
      lastProcessedMessage.content = lastProcessedMessage.content.slice(0, preTagStart + startTag.length) + processed +
        lastProcessedMessage.content.slice(preTagEnd);

    } else {
      const processed = hljs.highlightAuto(buffer).value;
      lastProcessedMessage.content += `${startTag}${processed}${endTag}`;
    }
  }

  updateLastProcessedMessage(content: string, session: ChatSession) {
    const lastMessage = session.processedMessages[session.processedMessages.length - 1];
    lastMessage.content = content;
  }

  getLastMessageChars(value: string, charCount: number) {
    if (value && value.length >= charCount) {
      return value.substring(value.length - charCount);
    }

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
    return str.split('\n\n').filter(e => e.length > 0).map(e => regex.exec(e)).map(p => {
      if (p === null) return {};
      return {
        type: p[1], value: p[2],
      };
    });
  }
}

export default ChatRenderer;
