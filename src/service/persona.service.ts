import * as os from 'os';
import * as path from 'path';
import * as fs from 'fs';
import {USER_DATA_DIRECTORY} from '../path-constants';
import Persona, {DEFAULT_PERSONA, PERSONA} from './to/persona';

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
