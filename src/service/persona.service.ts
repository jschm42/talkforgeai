import * as os from 'os';
import * as path from 'path';
import * as fs from 'fs';
import {PERSONA_DIRECTORY, USER_DATA_DIRECTORY} from '../path-constants';
import Persona, {DEFAULT_PERSONA, IMAGE_GENERATION_SYSTEM, PERSONA} from './to/persona';
import ChatMessage from './to/chat-message';
import Role from './to/role';
import {app} from 'electron';
import {promisify} from 'util';

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

// Function to check if a directory is empty
  async isDirEmpty(dir: string): Promise<boolean> {
    const readdir = promisify(fs.readdir);
    const files = await readdir(dir);
    return files.length === 0;
  };

  // Function to check if a directory exists
  async dirExists(dir: string): Promise<boolean> {
    try {
      await fs.promises.access(dir, fs.constants.F_OK);
      return true;
    } catch (err) {
      return false;
    }
  };

  // Function to read a file as JSON
  async readJsonFile(filePath: string): Promise<Persona> {
    const fileContent = await fs.promises.readFile(filePath, 'utf-8');
    console.log('Reading persona file: ' + filePath);
    return JSON.parse(fileContent);
  };

  async copyFilesToUserDir(): Promise<void> {
    const homeDirectory = os.homedir();
    const personaDirPath = path.join(homeDirectory, PERSONA_DIRECTORY);
    const appDirPath = app.getAppPath();

    try {
      // Create the "person" directory if it doesn't exist
      if (!(await this.dirExists(personaDirPath))) {
        await fs.promises.mkdir(personaDirPath, {recursive: true});
      }

      // Check if "person" directory exists
      if (await this.dirExists(personaDirPath)) {
        const sourceDir = path.join(appDirPath, '.webpack', 'persona');

        // Get all files in the app directory
        const files = await fs.promises.readdir(sourceDir);

        // Copy each file from app directory to user directory
        for (const file of files) {
          const sourceFilePath = path.join(sourceDir, file);
          const destFilePath = path.join(personaDirPath, file);
          console.log('Copying file: ' + file, sourceFilePath, destFilePath);
          await fs.promises.copyFile(sourceFilePath, destFilePath);
        }

        console.log('All files copied successfully!');
      } else {
        console.log('"person" directory is not empty!');
      }
    } catch (err) {
      console.error(`Error copying files: ${err}`);
    }
  };

  async readPersonas(): Promise<Array<Persona>> {
    let personaList: any[] = [DEFAULT_PERSONA];

    const homeDirectory = os.homedir();
    const personaDirPath = path.join(homeDirectory, PERSONA_DIRECTORY);

    try {
      const files = await fs.promises.readdir(personaDirPath);

      // Filter files by extension
      const personaFiles = files.filter(file => path.extname(file) === '.json');

      // Read each file
      const persona: Array<Persona> = await Promise.all(
        personaFiles.map(async file => {
          const filePath = path.join(personaDirPath, file);
          return await this.readJsonFile(filePath);
        }),
      );

      personaList = personaList.concat(persona);
      console.log('Loaded persona', personaList);
      return personaList;
    } catch (err) {
      console.error('Error reading directory:', err);
      throw err;
    }
  }
}

export default PersonaService;
