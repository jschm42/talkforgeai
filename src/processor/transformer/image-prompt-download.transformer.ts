import os from 'os';
import url from 'url';
import path from 'path';
import fs from 'fs';
import https from 'https';
import MessageTransformer from './transformer';
import OpenAiService from '../../service/openai.service';
import {CHAT_DATA_DIRECTORY} from '../../path-constants';
import IdentityUtil from '../../util/identity-util';

const UrlRegEx = /<image-prompt>[\\n]?([\s\S]*?)[\\n]?<\/image-prompt>/gm;

/**
 * Does not work, because local file path is not accessible from the browser
 */
class ImagePromptDownloadTransformer extends MessageTransformer {
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

      let matchAll = [...content.matchAll(UrlRegEx)];

      console.log('Processing images...', matchAll);
      this.sendProgress('Processing images...');

      Promise.all(matchAll.map(
        match => this.fetchAndDownloadImage(match[0], match[1]))).
        then((result) => {
          result.forEach(({fullTag, localFilePath}) => {
            content = content.replace(fullTag,
              `<img src="${localFilePath}">`);
          });

          console.log('Resolved content', content);
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

  async fetchAndDownloadImage(fullTag: string, prompt: string) {
    this.sendProgress('Processing image prompt: ' + prompt);
    const resultImageUrl = await this.#service.imageGeneration(prompt);
    console.log('Image result URL', resultImageUrl);
    const localFilePath = await this.downloadImage(resultImageUrl);
    console.log('Local file path', localFilePath);

    return {fullTag, localFilePath};
  }

  async downloadImage(imageUrl: string) {
    return new Promise((resolve, reject) => {
      const homeDirectory = os.homedir();

      const urlObj = url.parse(imageUrl);
      const fileName = IdentityUtil.generateUUID() + '.png';
      const subDirectoryPath = path.join(homeDirectory, CHAT_DATA_DIRECTORY,
        'images');
      const localFilePath = path.join(subDirectoryPath, fileName);

      fs.mkdirSync(subDirectoryPath, {recursive: true});

      //const localFilePath = path.join(__dirname, 'images', fileName);
      console.log('Writing image to ' + localFilePath);
      const fileStream = fs.createWriteStream(localFilePath);

      https.get(urlObj, (response) => {
        response.pipe(fileStream);
        fileStream.on('finish', () => {
          fileStream.close();
          resolve(localFilePath);
        });
        fileStream.on('error', (err) => {
          fs.unlink(localFilePath, () => reject(err));
        });
      }).on('error', (err) => {
        fs.unlink(localFilePath, () => reject(err));
      });
    });
  }
}

export default ImagePromptDownloadTransformer;
