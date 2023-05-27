class ChatGPTProperties {
  temperature = 0.7;
  maxTokens = 2048;
  frequencyPenalty = 0.0;
  presencePenalty = 0.0;
  topP = 1;
}

class ElevenLabsProperties {
  voiceId = '21m00Tcm4TlvDq8ikWAM'; // Rachel
  modelId = 'eleven_multilingual_v1';
}

class Persona {
  /**
   * Name of the profile
   */
  name: string;

  /**
   * Description of the profile
   */
  description: string;

  withImagePromptSystem = false;
  system: string;

  personaImage: string;

  chatGPTProperties: ChatGPTProperties = new ChatGPTProperties();

  elevenLabsProperties: ElevenLabsProperties = new ElevenLabsProperties();

  constructor(name: string, description: string, system: string, withImagePromptSystem = false) {
    this.name = name;
    this.description = description;
    this.system = system;
    this.withImagePromptSystem = withImagePromptSystem;
    this.personaImage = '';
  }
}

const IMAGE_GENERATION_SYSTEM = 'Create an image prompt in English to generate an image whenever instructed to output an image. ' +
  'The prompt should inspire creativity and imagination, and be vividly descriptive. Describe the scene from the view of a third-person stranger. For example, ' +
  '"A painting of a stormy sea with a lighthouse in the distance," or "A photograph of a city street at night, illuminated by neon lights.". ' +
  'Place the prompt between the following tags: <image-prompt></image-prompt>.';

// const DEFAULT_SYSTEM = 'Add a tag [[lang iso-code]] to the start of every message. ' +
//   'Create an image prompt in English for DALL-E to generate an image whenever instructed to output an image, ' +
//   'taking into account the content of the user prompts for subject matter, specific details, and style. ' +
//   'Place the prompt between the following tags: <image-prompt></image-prompt>.';

const DEFAULT_PERSONA = new Persona(
  'Default',
  'The default ChatGPT.',
  '',
  false,
);

const PERSONA = [
  DEFAULT_PERSONA,
];

export default Persona;
export {DEFAULT_PERSONA, PERSONA, IMAGE_GENERATION_SYSTEM, ElevenLabsProperties};
