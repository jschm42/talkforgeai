import * as os from 'os';
import * as path from 'path';
import * as fs from 'fs';
import {USER_DATA_DIRECTORY} from '../path-constants';
import Persona, {IMAGE_GENERATION_SYSTEM, PERSONA} from './to/persona';
import ChatMessage from './to/chat-message';
import Role from './to/role';

class PersonaService {

  readOrCreatePersona() {
    const fileName = `persona.json`;
    const homeDirectory = os.homedir();
    const subDirectoryPath = path.join(homeDirectory, USER_DATA_DIRECTORY);
    const filePath = path.join(subDirectoryPath, fileName);

    if (fs.existsSync(filePath)) {
      console.log('Reading persona from file: ' + filePath);
      return JSON.parse(
        fs.readFileSync(filePath, {encoding: 'utf-8'}));
    } else {
      console.log('Creating new persona file');
      fs.writeFileSync(filePath, JSON.stringify(PERSONA), {encoding: 'utf-8'});
      return PERSONA;
    }

  }

  /**
   * Get persona by name
   * @param name Persona name
   * @return Persona
   */
  getPersonaByName(name: string): Persona | undefined {
    return PERSONA.find(p => p.name === name);
  }

  getSystemMessagesForPersona(persona: Persona): Array<ChatMessage> {
    const systemMessages: Array<ChatMessage> = [];

    if (persona.withImagePromptSystem) {
      //systemMessages.push(new ChatMessage(Role.SYSTEM, IMAGE_GENERATION_SYSTEM));
    }

    //systemMessages.push(new ChatMessage(Role.SYSTEM, persona.system));

    systemMessages.push(new ChatMessage(Role.SYSTEM, IMAGE_GENERATION_SYSTEM));

    return systemMessages;
  }

  getPersonas() {
    return PERSONA;
  }
}

export default PersonaService;
