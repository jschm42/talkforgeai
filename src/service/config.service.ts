class OpenAIConfig {
  openAIApiKey = process.env.OPENAI_API_KEY;
  openAIUrl = 'https://api.openai.com/v1/completions';
  openAIChatUrl = 'https://api.openai.com/v1/chat/completions';
  openAIImageUrl = 'https://api.openai.com/v1/images/generations';

  postmanApiKey = process.env.POSTMAN_API_KEY;
  mockServerUrl = process.env.POSTMAN_MOCK_SERVER_URL;
  mockOpenAIUrl = process.env.POSTMAN_MOCK_SERVER_URL + '/v1/completions';
  mockOpenAIChatUrl = process.env.POSTMAN_MOCK_SERVER_URL + '/v1/chat/completions';
  mockOpenAIImageUrl = process.env.POSTMAN_MOCK_SERVER_URL + '/v1/images/generations';

  chatApiResponseId = process.env.POSTMAN_CHAT_API_RESPONSE_ID;
  imageApiResponseId = process.env.POSTMAN_IMAGE_API_RESPONSE_ID;

  testMode = false;
}

class Config {
  openai = new OpenAIConfig();
}

class ConfigService {
  config: Config;

  constructor() {
    this.config = new Config;
  }

  getConfig() {
    return this.config;
  }
}

export default ConfigService;
export {Config};
