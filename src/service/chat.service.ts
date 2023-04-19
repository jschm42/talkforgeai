import OpenAiService from './openai.service';
import UserMessageProcessor from '../processor/user-message-processor';
import AssistantMessageProcessor from '../processor/asssistant-message-processor';
import ChatMessage from './to/chat-message';
import Role from './to/role';
import os from 'os';
import path from 'path';
import {CHAT_DATA_DIRECTORY} from '../path-constants';
import fs from 'fs';
import ChatSession from './to/chat-session';

class ChatService {

  #openAiService;
  #userMessageProcessor;
  #assistantMessageProcessor;

  constructor() {
    this.#openAiService = new OpenAiService();
    this.#userMessageProcessor = new UserMessageProcessor();
    this.#assistantMessageProcessor = new AssistantMessageProcessor();

  }

  preProcess(prompt: string): Promise<ChatMessage> {
    return this.#userMessageProcessor.process(new ChatMessage(Role.USER, prompt));
  }

  submit(userMessage: ChatMessage, pastMessages: Array<ChatMessage>) {
    return this.createRequestPromise(userMessage, pastMessages).then((responseMessage: ChatMessage) => {
      return this.createPostProcessPromise(responseMessage);
    }).then(({originalAssistantMessage, processedAssistantMessage}) => {
      return {userMessage, originalAssistantMessage, processedAssistantMessage};
    });
  }

  createRequestPromise(promptMessage: ChatMessage, pastMessages: Array<ChatMessage>) {
    const requestMessages = [...pastMessages, promptMessage];

    console.log('Requesting response...');
    return this.#openAiService.chatCompletion(requestMessages).then((response: any) => {
      console.log('Response: ', response);
      return new ChatMessage(Role.ASSISTANT, response.content);
    });
  }

  createPostProcessPromise(originalAssistantMessage: ChatMessage) {
    console.log('Post-Processing response...');
    return this.#assistantMessageProcessor.process(originalAssistantMessage).
      then((processedAssistantMessage: ChatMessage) => {
        return {originalAssistantMessage, processedAssistantMessage};
      });
  }

  readFromFile(sessionId: string): ChatSession {

    // Read a json file from the user's home directory containing the chat
    const fileName = `chat-${sessionId}.json`;
    const homeDirectory = os.homedir();
    const subDirectoryPath = path.join(homeDirectory, CHAT_DATA_DIRECTORY,
      sessionId);
    const filePath = path.join(subDirectoryPath, fileName);

    console.log('Reading file ' + filePath, sessionId);
    const chatData = JSON.parse(fs.readFileSync(filePath, {encoding: 'utf-8'}));

    const chatSession = new ChatSession();

    chatSession.sessionId = chatData.sessionId;
    chatSession.messages = chatData.messages;
    chatSession.processedMessages = chatData.processedMessages;

    return chatSession;
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
