import * as fs from 'fs';
import * as path from 'path';
import * as os from 'os';
import {CHAT_DATA_DIRECTORY} from '../path-constants';

const CHAT_INDEX_FILE = 'chat-index.json';

class IndexEntry {
  sessionId: string;
  title: string;
  description: string;
  timestamp: Date;

  constructor(
      sessionId: string, title: string, description: string, timestamp: Date) {
    this.title = title;
    this.description = description;
    this.timestamp = timestamp;
    this.sessionId = sessionId;
  }
}

class ChatIndexService {

  #index: Array<IndexEntry> = [];

  get index() {
    return this.#index;
  }

  /**
   * Fins the index entry by the session id
   * @param sessionId
   * @returns {IndexEntry}
   */
  findBySessionId(sessionId: string) {
    return this.#index.find(entry => entry.sessionId === sessionId);
  }

  /**
   * Returns true, if the index contains an entry with the given session id
   * @param sessionId
   * @returns {boolean}
   */
  existsSessionId(sessionId: string) {
    return this.findBySessionId(sessionId) !== undefined;
  }

  /**
   * Reads the whole index file
   * @returns {Array<IndexEntry>} entries
   */
  read() {
    const filePath = this.getIndexFilePath();

    try {
      if (fs.existsSync(filePath)) {
        console.log('Reading index file', filePath);
        const data = fs.readFileSync(filePath, {encoding: 'utf8'});
        const parsed = JSON.parse(data);
        parsed.forEach((entry: IndexEntry) => {
          entry.timestamp = new Date(entry.timestamp);
        });
        this.#index = parsed;
      } else {
        console.log('ChatIndex file doesn\'t exist', filePath);
        this.#index = [];
      }
    } catch (err) {
      throw new Error('Error reading index file: ' + err);
    }

  }

  insert(sessionId: string, title: string): IndexEntry {
    const indexEntry = new IndexEntry(
        sessionId,
        title,
        '<empty>',
        new Date(),
    );
    this.#index.unshift(indexEntry);
    return indexEntry;
  }

  write() {
    //console.log('Writing index file', this.#index);

    // Read the index file
    const subDirectoryPath = this.getSubdirPath();
    const filePath = path.join(subDirectoryPath, CHAT_INDEX_FILE);

    fs.mkdirSync(subDirectoryPath, {recursive: true});

    try {
      fs.writeFileSync(filePath, JSON.stringify(this.#index));
      console.log('File has been written');
    } catch (error) {
      console.error(error);
    }
  }

  getIndexFilePath() {
    const subDirectoryPath = this.getSubdirPath();
    return path.join(subDirectoryPath, CHAT_INDEX_FILE);
  }

  getSubdirPath() {
    const homeDirectory = os.homedir();
    return path.join(homeDirectory, CHAT_DATA_DIRECTORY);
  }

}

export default ChatIndexService;
export {IndexEntry};