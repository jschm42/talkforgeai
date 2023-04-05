import * as os from 'os';
import * as path from 'path';
import * as fs from 'fs';
import {USER_DATA_DIRECTORY} from '../path-constants';

class Persona {
  /**
   * Name of the profile
   */
  name: string;

  /**
   * Description of the profile
   */
  description: string;

  temperature: number = 0.7;
  maxTokens: number = 2048;
  frequencyPenalty: number = 0.0;
  presencePenalty: number = 0.0;
  topP: number = 1;
  system: string;
  isDefault: boolean = false;

  constructor(
      name: string, description: string, system: string, isDefault = false) {
    this.name = name;
    this.description = description;
    this.system = system;
    this.isDefault = isDefault;
  }
}

const DEFAULT_PERSONA = new Persona(
    'Default',
    'The default ChatGPT.',
    '',
    true,
);

const PERSONA = [
  DEFAULT_PERSONA,
  new Persona(
      'Chat Bot',
      'The friendly chat bot that will answer your questions.',
      'Add a smiley to the end of every paragraph, covering the current mood.',
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
];

class PersonaService {
  #persona: Array<Persona> = [];

  get persona(): Array<Persona> {
    return this.#persona;
  }

  getDefaultPersona(): Persona {
    return this.#persona.find((persona: Persona) => persona.isDefault);
  }

  getPersonaByName(name: string): Persona {
    const entry = this.#persona.find(
        (persona: Persona) => persona.name === name);
    if (entry) {
      return entry;
    } else {
      throw Error('Persona not found: ' + name);
    }
  }

  readOrCreatePersona() {
    const fileName = `persona.json`;
    const homeDirectory = os.homedir();
    const subDirectoryPath = path.join(homeDirectory, USER_DATA_DIRECTORY);
    const filePath = path.join(subDirectoryPath, fileName);

    if (fs.existsSync(filePath)) {
      console.log('Reading persona from file: ' + filePath);
      this.#persona = JSON.parse(
          fs.readFileSync(filePath, {encoding: 'utf-8'}));
    } else {
      console.log('Creating new persona file');
      fs.writeFileSync(filePath, JSON.stringify(PERSONA), {encoding: 'utf-8'});
      this.#persona = PERSONA;
    }

  }
}

export default PersonaService;
export {Persona, DEFAULT_PERSONA, PERSONA};
