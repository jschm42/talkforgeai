import OpenAiService from '../../service/openai.service';
import MessageTransformer from './transformer';

const UrlRegEx = /<image-prompt>[\\n]?([\s\S]*?)[\\n]?<\/image-prompt>/gm;

class ImagePromptTransformer extends MessageTransformer {
  /**
   * @type {OpenAiService}
   */
  #service;

  constructor() {
    super();
    this.#service = new OpenAiService();
  }

  process() {
    return (content: string) => new Promise((resolve, reject) => {

      console.log('Processing images...', content);
      this.sendProgress('Processing images...');

      let matchAll = [...content.matchAll(UrlRegEx)];
      console.log('Image matches', matchAll);

      Promise.all(matchAll.map(match => this.fetchImage(match[0], match[1]))).
          then((result) => {
            result.forEach(({fullTag, resultImageUrl}) => {
              content = content.replace(fullTag,
                  `<img src="${resultImageUrl}">`);
            });

            resolve(content);
          });
    });
  }

  fetchImage(fullTag: string, prompt: string) {
    this.sendProgress('Processing image prompt: ' + prompt);
    return this.#service.imageGeneration(prompt).then((resultImageUrl) => {
      console.log('Image result URL', resultImageUrl);
      return {fullTag, resultImageUrl};
    });
  }

}

export default ImagePromptTransformer;