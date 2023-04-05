import MessageTransformer from './transformer';
import hljs from 'highlight.js';

class CodeBlockTransformer extends MessageTransformer {
  process() {
    return (content: string) => new Promise((resolve, reject) => {

      this.sendProgress('Processing code blocks...');

      const regex = /```([a-z]*)\n([\s\S]*?)```/gm;
      let m;

      while ((m = regex.exec(content)) !== null) {

        //let highlighted = rainbow.colorSync(m[2], m[1]);
        let highlightedValue;
        if (m[1] && m[1].length > 0) {
          highlightedValue = hljs.highlight(m[1], m[2]).value;
        } else {
          highlightedValue = hljs.highlightAuto(m[2]).value;
        }

        highlightedValue = `<pre>${highlightedValue}</pre>`;

        content = content.replace(m[0], highlightedValue);
        console.log('Replaced: ', content);
      }

      resolve(content);
    });

  }
}

export default CodeBlockTransformer;