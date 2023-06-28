import {convert} from 'html-to-text';

class HtmlToTextService {
  removeHtml(htmlContent: string): string {
    // See https://github.com/html-to-text/node-html-to-text/blob/master/packages/html-to-text/README.md
    const options = {
      decodeEntities: false,
      wordwrap: null,
      selectors: [
        //{ selector: 'a', options: { baseUrl: 'https://example.com' } },
        {selector: 'img', format: 'skip'},
      ],
    };

    return convert(htmlContent, options);
  }
}

export default HtmlToTextService;
