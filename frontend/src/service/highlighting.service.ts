import hljs from 'highlight.js';
import ChatMessage from '@/store/to/chat-message';

class HighlightingService {

  highlightCodeInChatMessage(message: Array<ChatMessage>) {
    message.forEach(m => {
      m.content = this.replaceCodeContent(m.content);
    });
  }

  replaceCodeContent(originalString: string) {
    // Use a regular expression to find the code tag and its content.
    const regex = /(<code class="(.*)">)([\s\S]*?)(<\/code>)/g;

    let newContent = originalString;
    let match;
    while ((match = regex.exec(originalString)) !== null) {
      let lang = match[2];
      if (lang && lang.startsWith('language-')) {
        lang = lang.substring(9);
      }

      const hljsLang = hljs.getLanguage(lang);
      let highlighted = '';
      if (hljsLang) {
        highlighted = hljs.highlight(lang, match[3]).value;
      } else {
        highlighted = hljs.highlightAuto(match[3]).value;
      }

      newContent = newContent.replace(match[0], `${match[1]}${highlighted}${match[4]}`);
    }

    return newContent;
  }

}

export default HighlightingService;
