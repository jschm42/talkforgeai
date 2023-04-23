class OpenAIConfig {
  openAIApiKey = process.env.OPENAI_API_KEY;
  chatApiResponseId = process.env.POSTMAN_CHAT_API_RESPONSE_ID;
  imageApiResponseId = process.env.POSTMAN_IMAGE_API_RESPONSE_ID;
  postmanApiKey = process.env.POSTMAN_API_KE;
  mockServerUrl = process.env.POSTMAN_MOCK_SERVER_URL;
  testMode = true;
}

class Config {
  openai = new OpenAIConfig()
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
