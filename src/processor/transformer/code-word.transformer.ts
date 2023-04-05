import hljs from 'highlight.js';
import MessageTransformer from './transformer';

class CodeWordTransformer extends MessageTransformer {

  process() {
    return (content: string) => new Promise((resolve, reject) => {

      this.sendProgress('Processing code words...');

      const regex = /`([^`]+)`/g;

      let m;

      let transformedContent = '';
      while ((m = regex.exec(content)) !== null) {
        console.log('Match 0: ', m[0]);
        console.log('Match 1: ', m[1]);

        //let highlighted = rainbow.colorSync(m[2], m[1]);
        let highlighted = hljs.highlightAuto(m[1]).value;
        let transformed = `<span class="code-word">${highlighted}</span>`;

        //const replaceStr = `<pre><code data-language=\"${m[1]}\">${m[2]}</code></pre>`;

        content = content.replace(m[0], transformed);
      }

      resolve(content);
    });

  }
}

export default CodeWordTransformer;
