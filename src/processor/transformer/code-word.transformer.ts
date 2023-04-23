import hljs from 'highlight.js';
import MessageTransformer from './transformer';

class CodeWordTransformer extends MessageTransformer {

  process() {
    return (content: string) => new Promise((resolve, reject) => {

      const regex = /`([^`]+)`/g;

      let m;

      let transformedContent = '';
      while ((m = regex.exec(content)) !== null) {
        let highlighted = hljs.highlightAuto(m[1]).value;
        let transformed = `<span class="code-word">${highlighted}</span>`;

        content = content.replace(m[0], transformed);
      }

      resolve(content);
    });

  }
}

export default CodeWordTransformer;
