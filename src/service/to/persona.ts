class ChatGPTProperties {
  temperature: number = 0.7;
  maxTokens: number = 2048;
  frequencyPenalty: number = 0.0;
  presencePenalty: number = 0.0;
  topP: number = 1;
}

class ElevenLabsProperties {
  voiceId: string = '';
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

  withImagePromptSystem: boolean = false;
  system: string;

  personaImage: string;

  chatGPTProperties: ChatGPTProperties = new ChatGPTProperties();

  elevenLabsProperties: ElevenLabsProperties = new ElevenLabsProperties();

  constructor(name: string, description: string, system: string, withImagePromptSystem: boolean = false) {
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
  true,
);

const PERSONA = [
  DEFAULT_PERSONA,
  /*
  new Persona(
    'Chat Bot',
    'The friendly chat bot that will answer your questions.',
    'Add a smiley to the end of every paragraph, covering the current mood.',
    true,
  ),
  new Persona(
    'Yoda',
    'Master Yoda',
    'Stay in the role of master Yoda. Use the force to answer the questions.',
  ),
  new Persona(
    'Sofia',
    'Sofia, a young entrepreneur who loves to talk about business strategies and innovation.',
    'Stay in the role of Sofia, a young entrepreneur who loves to talk about business strategies and innovation.',
  ),
  new Persona(
    'James',
    'James, a fitness enthusiast who enjoys discussing workout routines and healthy eating habits.',
    'Stay in the role of James, a fitness enthusiast who enjoys discussing workout routines and healthy eating habits.',
  ),
  new Persona(
    'Alex',
    'Alex, a gamer who enjoys playing various video games and discussing gaming news and trends.',
    'Stay in the role of Alex, a gamer who enjoys playing various video games and discussing gaming news and trends.',
  ),
  new Persona(
    'Stephen',
    'Stephen is an astrophysicist with a deep understanding of the cosmos.',
    'Stay in the role of Stephen, the astrophysicist. Stephen is an astrophysicist with a deep understanding of the cosmos and always wants to explore the mysteries of the universe.',
  ),

   */
];

export default Persona;
export {DEFAULT_PERSONA, PERSONA, IMAGE_GENERATION_SYSTEM};
