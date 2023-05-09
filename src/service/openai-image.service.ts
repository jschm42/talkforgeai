import {Config} from './config.service';

/**
 * @class OpenaiImageService
 */
class OpenaiImageService {
  #config: Config;

  constructor(config: Config) {
    this.#config = config;
  }

  imageGeneration(prompt: string) {
    return this.callOpenAiImageApi(
      this.#config.openai.testMode ? this.#config.openai.mockOpenAIImageUrl : this.#config.openai.openAIImageUrl,
      prompt);
  }

  async callOpenAiImageApi(url: string, prompt: string) {
    console.log('callOpenAiImageApi', url, prompt);
    const requestBody = {
      'prompt': prompt,
      'n': 1,
      'size': '512x512',
    };

    const requestOptions = this.#createRequestOptions(requestBody);
    console.log('Send request to OpenAI Image', requestOptions);
    return await this.#fetchImageApi(url, requestOptions);
  }

  async #fetchImageApi(url: string, requestOptions: any) {
    requestOptions.headers['x-mock-response-id'] = this.#config.openai.imageApiResponseId;

    console.log('Fetch Image API', url, requestOptions);

    const response = await fetch(url, requestOptions);
    if (response.ok) {
      const jsonResponse = await response.json();
      return jsonResponse.data[0].url;
    } else {
      throw new Error('HTTP error, status = ' + response.status);
    }
  }

  #createRequestOptions(requestBody: any) {
    return {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'x-api-key': this.#config.openai.postmanApiKey,
        'Authorization': `Bearer ${this.#config.openai.openAIApiKey}`,
      },
      body: JSON.stringify(requestBody),
    };
  }

}

export default OpenaiImageService;
