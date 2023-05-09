import ChatMessage from '../service/to/chat-message';
import {Config} from '../service/config.service';

enum OpenAiModel {
  chatGpt35Turbo = 'gpt-3.5-turbo',
  textDavinci3 = 'text-davinci-003',
  textCurie1 = 'text-curie-001',
  codeDavinci2 = 'code-davinci-002',
  codeCushman1 = 'code-cushman-001',
}

/**
 * @class OpenAiService
 */
class OpenAiRenderer {
  config: Config;

  constructor() {
    // @ts-ignore
    this.config = window.configAPI.getConfig();
  }

  async chatCompletion(messages: Array<ChatMessage>, stream = false) {
    const url = this.config.openai.testMode ?
      this.config.openai.mockServerUrl + this.config.openai.mockOpenAIChatUrl :
      this.config.openai.openAIChatUrl;
    return this.callOpenAiChatApi(url, messages, stream);
  }

  async imageGeneration(prompt: string) {
    const url = this.config.openai.testMode ?
      this.config.openai.mockServerUrl + this.config.openai.mockOpenAIImageUrl :
      this.config.openai.openAIImageUrl;
    return this.callOpenAiImageApi(url, prompt);
  }

  async callOpenAiChatApi(url: string, messages: Array<ChatMessage>, stream = false) {
    console.log('callOpenAiChatApi', url, messages);
    const requestBody = {
      model: OpenAiModel.chatGpt35Turbo,
      messages,
      'temperature': 0.7,
      'max_tokens': 2048,
      'top_p': 1.0,
      'frequency_penalty': 0.0,
      'presence_penalty': 0.0,
      stream,
    };

    const requestOptions = this.#createRequestOptions(requestBody);
    //console.log('Send request to OpenAI', requestOptions);

    if (stream) {
      return this.#fetchApiStream(url, requestOptions);
    } else {
      return this.#fetchChatApi(url, requestOptions);
    }
  }

  async callOpenAiApi(url: string, prompt: string) {
    console.log('callOpenAiApi', url, prompt);
    const requestBody = {
      model: OpenAiModel.textDavinci3,
      prompt: prompt,
      'temperature': 0,
      'max_tokens': 256,
      'top_p': 1.0,
      'frequency_penalty': 0.0,
      'presence_penalty': 0.0,
    };

    const requestOptions = this.#createRequestOptions(requestBody);
    return await this.#fetchApi(url, requestOptions);
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

  async #fetchApi(url: string, requestOptions: any) {
    console.log('Fetch API', url, requestOptions);
    const response = await fetch(url, requestOptions);
    if (response.ok) {
      const jsonResponse = await response.json();
      return jsonResponse.choices[0].text;
    } else {
      throw new Error('HTTP error, status = ' + response.status);
    }
  }

  async #fetchChatApi(url: string, requestOptions: any) {
    console.log('Fetch Chat API', url, requestOptions);

    const response = await fetch(url, requestOptions);
    if (response.ok) {
      const jsonResponse = await response.json();
      return jsonResponse.choices[0].message;
    } else {
      throw new Error('HTTP error, status = ' + response.status);
    }
  }

  async #fetchApiStream(url: string, requestOptions: any) {
    console.log('Fetch Chat API Stream', url, requestOptions);

    return fetch(url, requestOptions);
  }

  async #fetchImageApi(url: string, requestOptions: any) {
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
        'x-api-key': this.config.openai.postmanApiKey,
        'x-mock-response-id': this.config.openai.chatApiResponseId,
        'Authorization': `Bearer ${this.config.openai.openAIApiKey}`,
      },
      body: JSON.stringify(requestBody),
    };
  }

}

export default OpenAiRenderer;
