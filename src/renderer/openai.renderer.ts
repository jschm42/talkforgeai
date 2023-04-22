import ChatMessage from '../service/to/chat-message';

const openAIUrl = 'https://api.openai.com/v1/completions';
const openAIChatUrl = 'https://api.openai.com/v1/chat/completions';
const openAIImageUrl = 'https://api.openai.com/v1/images/generations';

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

  async chatCompletion(messages: Array<ChatMessage>, stream = false) {
    return this.callOpenAiChatApi(openAIChatUrl, messages, stream);
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
        //'x-api-key': postmanApiKey,
        'Authorization': `Bearer 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`,
      },
      body: JSON.stringify(requestBody),
    };
  }

}

export default OpenAiRenderer;
