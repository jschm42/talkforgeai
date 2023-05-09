import OpenaiImageService from './openai-image.service';
import UserMessageProcessor from '../processor/user-message-processor';
import AssistantMessageProcessor from '../processor/asssistant-message-processor';
import os from 'os';
import path from 'path';
import {CHAT_DATA_DIRECTORY} from '../path-constants';
import fs from 'fs';
import ChatSession from './to/chat-session';
import ConfigService from '../service/config.service';

class ChatService {
  #openAiService;
  #userMessageProcessor;
  #assistantMessageProcessor;

  constructor() {
    const config = new ConfigService();
    this.#openAiService = new OpenaiImageService(config.getConfig());
    this.#userMessageProcessor = new UserMessageProcessor();
    this.#assistantMessageProcessor = new AssistantMessageProcessor();

  }

  readFromFile(sessionId: string): ChatSession {

    // Read a json file from the user's home directory containing the chat
    const fileName = `chat-${sessionId}.json`;
    const homeDirectory = os.homedir();
    const subDirectoryPath = path.join(homeDirectory, CHAT_DATA_DIRECTORY,
      sessionId);
    const filePath = path.join(subDirectoryPath, fileName);

    console.log('Reading file ' + filePath, sessionId);
    return JSON.parse(fs.readFileSync(filePath, {encoding: 'utf-8'}));
  }

  writeToFile(chatSession: ChatSession) {
    console.log('Writing file');
    // Write a json file to the user's home directory containing the history
    // entries
    const fileName = `chat-${chatSession.sessionId}.json`;
    const data = JSON.stringify(chatSession, null, 2);

    // Get the user's home directory
    const homeDirectory = os.homedir();

    // Set the subdirectory name and create the directory if it does not exist
    const subDirectoryPath = path.join(homeDirectory, CHAT_DATA_DIRECTORY, chatSession.sessionId);

    fs.mkdirSync(subDirectoryPath, {recursive: true});

    // Set the file path and name
    const filePath = path.join(subDirectoryPath, fileName);

    try {
      fs.writeFileSync(filePath, data, {encoding: 'utf-8'});
    } catch (error) {
      console.log('Error writing file', error);
    }
  }

}

export default ChatService;
