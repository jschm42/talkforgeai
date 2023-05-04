import ChatMessage from '../service/to/chat-message';
import Role from '../service/to/role';
import OpenAiRenderer from './openai.renderer';
import ChatSession from '../service/to/chat-session';
import hljs from 'highlight.js';
import {toRaw} from 'vue';
import Mustache from 'mustache';

const regExOptimized = /{"(content|role)":(.*?)},/;

const newlineRegex = /\\n/g;
const tabRegex = /\\t/g;

const commandConfig = {
  mode: 'commandMode',
  buffer: 'commandBuffer',
  startTag: '<div class="code-block card shadow p-2 my-3"><div class="card-body"><h5 class="card-title">{{lang}}</h5><pre>',
  endTag: '</pre></div></div>',
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

class ChatRendererOptimized {
  openAiService = new OpenAiRenderer();
  commandMode = false;
  wordMode = false;
  commandBuffer = '';
  wordBuffer = '';
  imageMode = false;
  imageBuffer = '';
  xBuffer = '';
  processedStartTag = '';

  async submit(prompt: string, session: ChatSession) {
    const previousMessages = this.getPreviousMessages(session);

    const userMessage = new ChatMessage(Role.USER, prompt);
    session.messages.push(userMessage);
    // @ts-ignore
    const processedUserMessage = await window.chatAPI.processUserMessage(userMessage);
    session.processedMessages.push(processedUserMessage);

    const submitMessages = [...session.systemMessages, ...previousMessages, userMessage];

    const response = await this.openAiService.chatCompletion(submitMessages, true);
    const reader = response.body.getReader();

    session.processedMessages.push(new ChatMessage(Role.ASSISTANT, ''));

    await this.processReader(reader, session);

    await this.postProcessLastMessage(session);

    // @ts-ignore
    window.chatAPI.writeChatSession(toRaw(session));
  }

  processReader = async (reader: any, session: ChatSession) => {
    const decoder = new TextDecoder();
    let done = false;
    let messageContent = '';

    while (!done) {
      const row = await reader.read();
      const str = decoder.decode(row.value, {stream: true});
      const parsed = this.parseStreamResponse(str);
      const contentArray = parsed.filter((e: any) => e.type === 'content').map((e: any) => e.value);

      for (const value of contentArray) {
        //console.log('VALUE', value);
        if (value) {
          messageContent += value;
          await this.handleMessageContent(value, messageContent, session);
        }
      }

      done = row.done;
    }

    console.log('Stream complete');
    session.messages.push(new ChatMessage(Role.ASSISTANT, messageContent));
  };

  async postProcessLastMessage(session: ChatSession) {
    const lastProcessedMessage = session.processedMessages.slice(-1)[0];

    // @ts-ignore
    const processedMessage = await window.transformerAPI.processAssistantMessage(toRaw(lastProcessedMessage));

    lastProcessedMessage.content = processedMessage.content;
  }

  async handleMessageContent(value: string, messageContent: string, session: ChatSession) {

    this.xBuffer += value;
    //console.log('VALUE', value);

    if (!this.commandMode && this.xBuffer.lastIndexOf('```') > -1) {
      console.log('Command Mode ON');
      this.commandMode = true;
      this.commandBuffer = '';
      this.xBuffer = '';
    } else if (this.commandMode && this.xBuffer.lastIndexOf('```') > -1) {
      console.log('Command Mode OFF', this.xBuffer);
      this.commandMode = false;
      this.xBuffer = '';
      this.processedStartTag = '';
    }

    if (this.commandMode) {
      this.commandBuffer += value;
      //console.log('APPEND OR REPLACE', this.commandBuffer);
      this.appendOrReplaceTagInMessage(this.commandBuffer, commandConfig.startTag, commandConfig.endTag, session);
    } else {
      this.addToLastMessage(value, session);
    }
  }

  getPreviousMessages(session: ChatSession) {
    const previousMessages = [new ChatMessage(Role.SYSTEM, session.persona.system)];
    session.messages.forEach((message, i) => {
      if (message.role === Role.USER) {
        previousMessages.push(Object.assign({}, session.processedMessages[i]));
      } else {
        previousMessages.push(Object.assign({}, message));
      }
    });
    return previousMessages;
  }

  addToLastMessage(content: string, session: ChatSession) {
    content = content.replace(/\\n\\n/g, '\n\n').replace(/\\n/g, '\n').replace(/`/g, '');

    const lastProcessedMessage = session.processedMessages.slice(-1)[0];
    const lastMessage = session.messages.slice(-1)[0];

    lastProcessedMessage.content += content;
  }

  async handleImagePrompt(buffer: string, session: ChatSession) {
    const lastProcessedMessage = session.processedMessages.slice(-1)[0];

    const startTag = '<image-prompt>';
    const endTag = '</image-prompt>';

    const preTagStart = lastProcessedMessage.content.lastIndexOf(startTag);
    const preTagEnd = lastProcessedMessage.content.lastIndexOf(endTag);

    // @ts-ignore
    const processed = await window.transformerAPI.transformImage(buffer);

    console.log('LAST PROC MESSAGE', lastProcessedMessage.content);
    //lastProcessedMessage.content = lastProcessedMessage.content.slice(0, preTagStart + startTag.length) + processed +
    // lastProcessedMessage.content.slice(preTagEnd);

    lastProcessedMessage.content = lastProcessedMessage.content.slice(0, preTagStart) + processed +
      lastProcessedMessage.content.slice(preTagEnd);

    console.log('LAST PROC MESSAGE (AFTER)', lastProcessedMessage.content);

  }

  appendOrReplaceTagInMessage(buffer: string, startTag: string, endTag: string, session: ChatSession): void {
    buffer = buffer.replace(/``$/, '');
    const indexEndLangToken = buffer.indexOf('\\n');

    if (indexEndLangToken > -1) {
      const lang = buffer.slice(0, indexEndLangToken).replace(/`/g, '');
      buffer = buffer.slice(indexEndLangToken + 2);

      const hljsLanguage = hljs.getLanguage(lang);

      if (this.processedStartTag.length === 0) {
        this.processedStartTag = Mustache.render(startTag, {lang: hljsLanguage?.name || 'Code'});
      }

      const lastProcessedMessage = session.processedMessages.slice(-1)[0];

      buffer = buffer.replace(newlineRegex, '\n').replace(tabRegex, '\t');

      const processed = hljsLanguage?.name
        ? hljs.highlight(buffer, {language: hljsLanguage.name}).value
        : hljs.highlightAuto(buffer).value;

      if (lastProcessedMessage.content.endsWith(endTag)) {
        const preTagStart = lastProcessedMessage.content.lastIndexOf(this.processedStartTag);
        const preTagEnd = lastProcessedMessage.content.lastIndexOf(endTag);

        lastProcessedMessage.content = lastProcessedMessage.content.slice(0,
          preTagStart + this.processedStartTag.length) + processed + lastProcessedMessage.content.slice(preTagEnd);
      } else {
        lastProcessedMessage.content += `${this.processedStartTag}${processed}${endTag}`;
      }
    }

  }

  parseStreamResponse(str: string) {
    // FIXME Does not parse the content corretly, if a " is inside
    return str.split('\n\n').filter(e => e.length > 0).map(e => regExOptimized.exec(e)).map(p => {
      if (p === null) return {};

      // Fix for \\"
      p[2] = p[2].replace(/\\"/, '"');

      //console.log('RAW', p);

      const result = {type: this.removeFirstAndLastQuotes(p[1]), value: this.removeFirstAndLastQuotes(p[2])};

      //console.log('PARSED', result.value);

      return result;
    });
  }

  removeFirstAndLastQuotes(str: string): string {
    if (str.startsWith('"')) {
      str = str.slice(1);
    }
    if (str.endsWith('"')) {
      str = str.slice(0, -1);
    }
    return str;
  }
}

export default ChatRendererOptimized;

