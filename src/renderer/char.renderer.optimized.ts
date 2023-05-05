import ChatMessage from '../service/to/chat-message';
import Role from '../service/to/role';
import OpenAiRenderer from './openai.renderer';
import ChatSession from '../service/to/chat-session';
import hljs from 'highlight.js';
import {toRaw} from 'vue';
import Mustache from 'mustache';

const STREAM_REGEX = /{"(content|role)":(.*?)},/;
const CODETAG_REGEX = /`(.*)`/;
const NEWLINE_REGEX = /\\n/g;
const TAB_REGEX = /\\t/g;
const BACKTICK_REGEX = /`/g;
const DOUBLEBACKTICK_EL_REGEX = /``$/;
const QUOTE_REGEX = /\\"/;

const CODEBLOCK_START_TPL = `<div class="code-block card shadow p-2 my-3"><div class="card-body"><h5 class="card-title">{{lang}}</h5><pre>`;
const CODEBLOCK_END_TPL = `</pre></div></div>`;

const TRIPLE_BACKTICK = '```';
const NEW_LINE = '\\n';

class ChatRendererOptimized {
  openAiService = new OpenAiRenderer();
  codeBlockMode = false;
  codeBlockBuffer = '';
  htmlBlockMode = false;
  htmlBlockBuffer = '';

  sectionBuffer = '';
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
    this.sectionBuffer += value;
    console.log('VALUE', value);
    //console.log('SEC BUF', this.sectionBuffer);

    if (!this.codeBlockMode && this.sectionBuffer.lastIndexOf(TRIPLE_BACKTICK) > -1) {
      this.codeBlockMode = true;
      this.codeBlockBuffer = '';
      this.sectionBuffer = '';
    } else if (this.codeBlockMode && this.sectionBuffer.lastIndexOf(TRIPLE_BACKTICK) > -1) {
      console.log('HTML-MODE OFF');
      this.codeBlockMode = false;
      this.sectionBuffer = '';
      this.processedStartTag = '';
      value = '';
    } else if (!this.htmlBlockMode && this.sectionBuffer.lastIndexOf('<!') > -1) {
      console.log('HTML-MODE ON');
      this.htmlBlockMode = true;
      this.htmlBlockBuffer = '';
      this.sectionBuffer = '';
    } else if (this.htmlBlockMode && this.sectionBuffer.lastIndexOf('</html>') > -1) {
      console.log('HTML-MODE OFF');
      this.htmlBlockMode = false;
      this.sectionBuffer = '';
      this.htmlBlockBuffer += value;
      this.processHtmlBlock(this.htmlBlockBuffer, '<pre>', '</pre>', session);
      value = '';
    }

    if (this.codeBlockMode) {
      this.codeBlockBuffer += value;
      this.processCodeBlock(this.codeBlockBuffer, CODEBLOCK_START_TPL, CODEBLOCK_END_TPL, session);
    } else if (this.htmlBlockMode) {
      this.htmlBlockBuffer += value;
      this.processHtmlBlock(this.htmlBlockBuffer, '<pre>', '</pre>', session);
    } else {
      this.addToLastMessage(value, session);
      this.postProcessCode(session);
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
    content = content.replace(NEWLINE_REGEX, '\n');

    const lastProcessedMessage = session.processedMessages.slice(-1)[0];
    lastProcessedMessage.content += content;
  }

  postProcessCode(session: ChatSession): void {
    const lastProcessedMessage = session.processedMessages.slice(-1)[0];

    const lastContent = lastProcessedMessage.content;
    const matched = lastContent.match(CODETAG_REGEX);
    if (matched) {
      const highlighted = hljs.highlightAuto(matched[1]).value;
      lastProcessedMessage.content = lastContent.replace(matched[0], `<code>${highlighted}</code>`);
    }
  }

  processHtmlBlock(buffer: string, startTag: string, endTag: string, session: ChatSession): void {
    console.log('BUFFER', buffer);

    const lastProcessedMessage = session.processedMessages.slice(-1)[0];

    buffer = buffer.replace(NEWLINE_REGEX, '\n').replace(TAB_REGEX, '\t');

    const processed = hljs.highlight(buffer, {language: 'html'}).value;

    if (lastProcessedMessage.content.endsWith(endTag)) {
      const preTagStart = lastProcessedMessage.content.lastIndexOf(startTag);
      const preTagEnd = lastProcessedMessage.content.lastIndexOf(endTag);

      lastProcessedMessage.content = lastProcessedMessage.content.slice(0,
        preTagStart + startTag.length) + processed + lastProcessedMessage.content.slice(preTagEnd);
    } else {
      lastProcessedMessage.content += `${startTag}${processed}${endTag}`;
    }

  }

  processCodeBlock(buffer: string, startTag: string, endTag: string, session: ChatSession): void {
    buffer = buffer.replace(DOUBLEBACKTICK_EL_REGEX, '');
    const indexEndLangToken = buffer.indexOf(NEW_LINE);

    console.log('BUFFER', buffer);

    if (indexEndLangToken > -1) {
      const lang = buffer.slice(0, indexEndLangToken).replace(BACKTICK_REGEX, '');
      console.log('LANG', lang);
      buffer = buffer.slice(indexEndLangToken + 2);

      const hljsLanguage = hljs.getLanguage(lang);
      console.log('HLJS', hljsLanguage);

      if (this.processedStartTag.length === 0) {
        this.processedStartTag = Mustache.render(startTag, {lang: hljsLanguage?.name || 'Code'});
      }

      const lastProcessedMessage = session.processedMessages.slice(-1)[0];

      buffer = buffer.replace(NEWLINE_REGEX, '\n').replace(TAB_REGEX, '\t');

      let processed;
      if (hljsLanguage && hljsLanguage.name) {
        processed = hljs.highlight(buffer, {language: lang}).value;
      } else {
        processed = hljs.highlightAuto(buffer).value;
      }

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
    return str.split('\n\n').filter(e => e.length > 0).map(e => STREAM_REGEX.exec(e)).map(p => {
      if (p === null) return {};

      // Fix for \\"
      p[2] = p[2].replace(QUOTE_REGEX, '"');

      return {type: this.removeFirstAndLastQuotes(p[1]), value: this.removeFirstAndLastQuotes(p[2])};
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

