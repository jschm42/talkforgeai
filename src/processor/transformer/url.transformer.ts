import https from 'https';
import http from 'http';
import MessageTransformer from './transformer';

const convert = require('html-to-text');

const UrlRegEx = /\[\[link\s(https?:\/\/[^\s\]]+)\]\]/g;

class UrlTransformer extends MessageTransformer {

  process() {
    return (content: string) => new Promise((resolve, reject) => {

      console.log('UrlTransformer.process', content);

      let matchAll = [...content.matchAll(UrlRegEx)];

      const urls = matchAll.map(match => {
        return {
          tag: match[0],
          url: match[1],
        };
      });

      const fetchPromises = urls.map(({tag, url}) => {
        return this.fetchHTML(tag, url);
      });

      Promise.all(fetchPromises).then((result) => {
        console.log('UrlTransformer.afterFetch', result);

        result.forEach((entry: any) => {

          const options = {
            limits: {
              maxDepth: 30,
            },
            selectors: [
              {selector: 'div', options: {trimEmptyLines: true}},
              {selector: 'a', format: 'skip'},
            ],
          };

          let textContent = convert(entry.data, options);
          textContent = textContent.replaceAll('\n\n', '\n').replaceAll('* \n', '');

          content = content.replace(entry.tag, '>>>' + textContent + '<<<');
        });

        resolve(content);
      });

    });
  }

  fetchHTML(tag: string, url: string) {
    const protocol = url.startsWith('https') ? https : http;

    return new Promise((resolve, reject) => {
      protocol.get(url, (res) => {
        let data = '';

        res.on('data', (chunk) => {
          data += chunk;
        });

        res.on('end', () => {
          resolve({tag, url, data});
        });

      }).on('error', (err) => {
        reject(err);
      });
    });
  }

}

export default UrlTransformer;
