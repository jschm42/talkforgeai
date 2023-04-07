import * as fs from 'fs';
import * as path from 'path';
import * as os from 'os';
import {CHAT_DATA_DIRECTORY} from '../path-constants';
import IndexEntry from './to/index-entry';

const CHAT_INDEX_FILE = 'chat-index.json';

class ChatIndexService {

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
        return parsed;
      } else {
        console.log('ChatIndex file doesn\'t exist', filePath);
        return [];
      }
    } catch (err) {
      throw new Error('Error reading index file: ' + err);
    }

  }

  insert(indexEntries: Array<IndexEntry>, sessionId: string, title: string): IndexEntry {
    const indexEntry = new IndexEntry(
      sessionId,
      title,
      '<empty>',
      new Date(),
    );
    indexEntries.unshift(indexEntry);
    return indexEntry;
  }

  write(indexEntries: Array<IndexEntry>) {
    //console.log('Writing index file', this.#index);

    // Read the index file
    const subDirectoryPath = this.getSubdirPath();
    const filePath = path.join(subDirectoryPath, CHAT_INDEX_FILE);

    fs.mkdirSync(subDirectoryPath, {recursive: true});

    try {
      fs.writeFileSync(filePath, JSON.stringify(indexEntries));
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
