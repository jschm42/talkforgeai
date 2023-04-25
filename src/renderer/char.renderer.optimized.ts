import ChatMessage from '../service/to/chat-message';
import Role from '../service/to/role';
import OpenAiRenderer from './openai.renderer';
import ChatSession from '../service/to/chat-session';
import hljs from 'highlight.js';

const regex = /"delta":\s*{"(role|content)":"([^"]+)"/;

const regExOptimized = /{"(content|role)":(.*?)}/;

class ChatRendererOptimized {
  openAiService = new OpenAiRenderer();
  commandMode = false;
  wordMode = false;
  commandBuffer = '';
  wordBuffer = '';

  async submit(prompt: string, session: ChatSession) {
    const previousMessages = this.getPreviousMessages(session);
    const userMessage = this.addMessageToSession(prompt, Role.USER, session);
    const submitMessages = [...previousMessages, userMessage];

    const response = await this.openAiService.chatCompletion(submitMessages, true);
    const reader = response.body.getReader();

    const processedMessage = this.addMessageToSession('', Role.ASSISTANT, session, true);
    const message = this.addMessageToSession('', Role.ASSISTANT, session);

    await this.processReader(reader, session);
  }

  processReader = async (reader: any, session: ChatSession) => {
    const decoder = new TextDecoder();
    let done = false;
    let messageContent = '';

    while (!done) {
      const row = await reader.read();
      const str = decoder.decode(row.value, {stream: true});
      console.log('PARSED', str);
      const parsed = this.parseStreamResponse(str);
      const contentArray = parsed.filter(e => e.type === 'content').map(e => e.value);

      for (let value of contentArray) {
        if (value) {
          messageContent += value;
          this.handleMessageContent(value, messageContent, session);
        }
      }

      done = row.done;
    }

    console.log('Stream complete');
    session.messages.push(new ChatMessage(Role.ASSISTANT, messageContent));
  };

  handleMessageContent(value: string, messageContent: string, session: ChatSession) {
    const commandConfig = {
      mode: 'commandMode',
      buffer: 'commandBuffer',
      startTag: '<pre>',
      endTag: '</pre>',
      regExStart: /```[a-z]*\\n$/,
      regExEnd: /```\\n\\n$/,
    };

    const wordConfig = {
      mode: 'wordMode',
      buffer: 'wordBuffer',
      startTag: '<code>',
      endTag: '</code>',
      regExStart: / `$/,
      regExEnd: /`$/,
    };

    if (messageContent.match(commandConfig.regExStart)) {
      this.toggleMode(commandConfig, true, session);
    } else if (messageContent.match(commandConfig.regExEnd)) {
      this.toggleMode(commandConfig, false, session);
    } else if (messageContent.match(wordConfig.regExStart)) {
      this.toggleMode(wordConfig, true, session);
    } else if (messageContent.match(wordConfig.regExEnd)) {
      this.toggleMode(wordConfig, false, session);
    } else {
      this.addToLastMessage(value, session);
    }
  }

  toggleMode(config: any, isOn: boolean, session: ChatSession) {
    const {mode, buffer, startTag, endTag} = config;

    // @ts-ignore
    this[mode] = isOn;

    if (isOn) {
      // @ts-ignore
      this[buffer] = '';
    } else {
      // @ts-ignore
      this.appendOrReplaceTagInMessage(this[buffer], startTag, endTag, session);
      // @ts-ignore
      this[buffer] = '';
    }
  }

  getPreviousMessages(session: ChatSession) {
    const previousMessages = [new ChatMessage(Role.SYSTEM, session.system)];
    session.messages.forEach((message, i) => {
      if (message.role === Role.USER) {
        previousMessages.push(Object.assign({}, session.processedMessages[i]));
      } else {
        previousMessages.push(Object.assign({}, message));
      }
    });
    return previousMessages;
  }

  addMessageToSession(content: string, role: Role, session: ChatSession, isProcessed = false) {
    const message = new ChatMessage(role, content);
    if (isProcessed) {
      session.processedMessages.push(message);
    } else {
      session.messages.push(message);
    }
    return message;
  }

  addToLastMessage(content: string, session: ChatSession) {
    content = content.replace(/\n\n/g, '\n\n').replace(/\n/g, '\n').replace(/\\/g, '"').replace(/`/g, '');

    const lastProcessedMessage = session.processedMessages.slice(-1)[0];
    const lastMessage = session.messages.slice(-1)[0];

    lastProcessedMessage.content += content;
    lastMessage.content += content;
  }

  appendOrReplaceTagInMessage(buffer: string, startTag: string, endTag: string, session: ChatSession) {
    buffer = buffer.replace(/\n\n/g, '\n\n').
      replace(/\n/g, '\n').
      replace(/`/g, '').
      replace(/\t/g, '\t').
      replace(/\\/g, '"');

    const lastProcessedMessage = session.processedMessages.slice(-1)[0];

    if (lastProcessedMessage.content.endsWith(endTag)) {
      const preTagStart = lastProcessedMessage.content.lastIndexOf(startTag);
      const preTagEnd = lastProcessedMessage.content.lastIndexOf(endTag);

      const processed = hljs.highlightAuto(buffer).value;

      lastProcessedMessage.content = lastProcessedMessage.content.slice(0, preTagStart + startTag.length) + processed +
        lastProcessedMessage.content.slice(preTagEnd);

    } else {
      const processed = hljs.highlightAuto(buffer).value;
      lastProcessedMessage.content += `${startTag}${processed}${endTag}`;
    }

  }

  parseStreamResponse(str: string) {
    // FIXME Does not parse the content corretly, if a " is inside
    return str.split('\n\n').filter(e => e.length > 0).map(e => regExOptimized.exec(e)).map(p => {
      if (p === null) return {};

      // Fix for \\"
      p[2] = p[2].replace(/\\"/, '"');

      return {
        type: this.removeFirstAndLastQuotes(p[1]), value: this.removeFirstAndLastQuotes(p[2]),
      };
    });
  }

  removeFirstAndLastQuotes(str: string): string {
    if (str.length >= 2 && str.startsWith('"') && str.endsWith('"')) {
      return str.slice(1, -1);
    }
    return str;
  }
}

export default ChatRendererOptimized;

