import * as os from 'os';
import * as path from 'path';
import * as fs from 'fs';
import {USER_DATA_DIRECTORY} from '../path-constants';
import {PERSONA} from './to/persona';

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
}

export default PersonaService;
