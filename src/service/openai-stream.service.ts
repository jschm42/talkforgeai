import {OpenAIExt} from 'openai-ext';

const openAIApiKey = process.env.OPENAI_API_KEY;
const openAIUrl = 'https://api.openai.com/v1/completions';
const openAIChatUrl = 'https://api.openai.com/v1/chat/completions';
const openAIImageUrl = 'https://api.openai.com/v1/images/generations';

const postmanApiKey = process.env.POSTMAN_API_KEY;
const mockServerUrl = process.env.POSTMAN_MOCK_SERVER_URL;
const mockOpenAIUrl = mockServerUrl + '/v1/completions';
const mockOpenAIChatUrl = mockServerUrl + '/v1/chat/completions';
const mockOpenAIImageUrl = mockServerUrl + '/v1/images/generations';

const chatApiResponseId = process.env.POSTMAN_CHAT_API_RESPONSE_ID;
const imageApiResponseId = process.env.POSTMAN_IMAGE_API_RESPONSE_ID;

const testMode = false;

enum OpenAiModel {
  chatGpt35Turbo = 'gpt-3.5-turbo',
  textDavinci3 = 'text-davinci-003',
  textCurie1 = 'text-curie-001',
  codeDavinci2 = 'code-davinci-002',
  codeCushman1 = 'code-cushman-001',
}

// Configure the stream (use type ClientStreamChatCompletionConfig for TypeScript users)
const streamConfig: any = {
  apiKey: openAIApiKey, // Your API key
  handler: {
    // Content contains the string draft, which may be partial. When isFinal is true, the completion is done.
    onContent(content: any, isFinal: any, xhr: any) {
      console.log(content, 'isFinal?', isFinal);
    },
    onDone(xhr: any) {
      console.log('Done!');
    },
    onError(error: any, status: any, xhr: any) {
      console.error(error);
    },
  },
};

/**
 * @class OpenAiStreamService
 */
class OpenAiStreamService {

  stream() {
    return OpenAIExt.streamClientChatCompletion(
      {
        model: 'gpt-3.5-turbo',
        messages: [
          {role: 'system', content: 'You are a helpful assistant.'},
          {role: 'user', content: 'Tell me a funny joke.'},
        ],
      },
      streamConfig,
    );

  }
}

export default OpenAiStreamService;
