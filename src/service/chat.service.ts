import {DEFAULT_PERSONA, Persona} from './persona.service';
import {CHAT_DATA_DIRECTORY} from '../path-constants';
import OpenAiService from './openai.service';
import UserMessageProcessor from '../processor/user-message-processor';
import AssistantMessageProcessor from '../processor/asssistant-message-processor';
import IdentityUtil from '../util/identity-util';
import os from 'os';
import path from 'path';
import fs from 'fs';

const DEFAULT_SYSTEM = 'Add a tag [[lang iso-code]] to the start of every message. ' +
  'Create an image prompt in English for DALL-E to generate an image whenever instructed to output an image, ' +
  'taking into account the content of the user prompts for subject matter, specific details, and style. ' +
  'Place the prompt between the following tags: <image-prompt></image-prompt>.';

enum Role {
  USER = 'user',
  SYSTEM = 'system',
  ASSISTANT = 'assistant'
}

class ChatMessage {

  role: Role;

  content: string;

  constructor(role: Role, content: string) {
    this.role = role;
    this.content = content;
  }
}

class ChatService {

  #sessionId: string = null;
  #messages: Array<ChatMessage> = [];
  #processedMessages: Array<ChatMessage> = [];
  #persona: Persona = null;
  #openAiService;
  #userMessageProcessor;
  #assistantMessageProcessor;
  #isSubmitted: boolean = false;

  constructor() {
    this.#openAiService = new OpenAiService();
    this.#userMessageProcessor = new UserMessageProcessor();
    this.#assistantMessageProcessor = new AssistantMessageProcessor();

    this.new();
  }

  get sessionId(): string {
    return this.#sessionId;
  }

  get persona(): Persona {
    return this.#persona;
  }

  get isSubmitted(): boolean {
    return this.#isSubmitted;
  }

  get messages(): Array<ChatMessage> {
    return this.#messages;
  }

  get processedMessages(): Array<ChatMessage> {
    return this.#processedMessages;
  }

  new() {
    this.#sessionId = IdentityUtil.generateUUID();
    this.#isSubmitted = false;
    this.#persona = DEFAULT_PERSONA;
    this.clearMessages();
  }

  sendProgress(progress: string) {
    //ipcMain.emit('update-progress', progress);
  }

  setPersona(persona: Persona) {
    console.log('setPersona', persona);
    this.#persona = persona;

    const system = DEFAULT_SYSTEM + ' ' + persona.system;

    const index = this.#messages.findIndex(
      message => message.role === Role.SYSTEM);

    if (index > -1) {
      this.#messages[index] = new ChatMessage(Role.SYSTEM, system);
      this.#processedMessages[index] = new ChatMessage(Role.SYSTEM, system);
    } else {
      this.#messages.splice(0, 0, new ChatMessage(Role.SYSTEM, system));
      this.#processedMessages.splice(0, 0,
        new ChatMessage(Role.SYSTEM, system));
    }

  }

  submit(prompt: string, pastMessages: Array<ChatMessage>) {
    const promptMessage = new ChatMessage(Role.USER, prompt);

    this.sendProgress('Processing prompt...');
    return this.#userMessageProcessor.process(promptMessage).then((processedPromptMessage: ChatMessage) => {
      return {promptMessage, processedPromptMessage};
      //this.#addMessage(promptMessage);
      //this.#addProcessedMessage(processedPromptMessage);
    }).then(() => {
      return this.createRequestPromise(pastMessages);
    }).then((message: ChatMessage) => {
      return this.createPostProcessPromise(message);
    }).then((processedMessage: ChatMessage) => {
      this.#writeToFile();
      return processedMessage;
    });
  }

  chatRequest(prompt: string) {
    this.#isSubmitted = true;
    const promptMessage = new ChatMessage(Role.USER, prompt);

    this.sendProgress('Processing prompt...');
    return this.#userMessageProcessor.process(promptMessage).
      then((processedPromptMessage: ChatMessage) => {
        this.#addMessage(promptMessage);
        this.#addProcessedMessage(processedPromptMessage);
      }).
      then(() => {
        return this.createRequestPromise(this.getRequestMessages());
      }).
      then((message: ChatMessage) => {
        return this.createPostProcessPromise(message);
      }).
      then((processedMessage: ChatMessage) => {
        this.#writeToFile();
        return processedMessage;
      });
  }

  getRequestMessages(): Array<ChatMessage> {
    const messages = [];
    for (let i = 0; i < this.#messages.length; i++) {
      if (this.#messages[i].role === Role.USER) {
        messages.push(this.#processedMessages[i]);
      } else {
        messages.push(this.#messages[i]);
      }
    }
    return messages;
  }

  getDisplayMessages(): Array<ChatMessage> {
    const messages = [];
    for (let i = 0; i < this.#messages.length; i++) {
      if (this.#messages[i].role === Role.USER) {
        messages.push(this.#messages[i]);
      } else {
        messages.push(this.#processedMessages[i]);
      }
    }
    return messages;
  }

  createRequestPromise(messageRequest: Array<ChatMessage>) {
    this.sendProgress('Processing request...');
    return this.#openAiService.chatCompletion(messageRequest).
      then((response: any) => {
        console.log('Response: ', response);

        const chatMessage = new ChatMessage(Role.ASSISTANT, response.content);
        this.#addMessage(chatMessage);

        return chatMessage;
      });
  }

  createPostProcessPromise(message: ChatMessage) {
    console.log('Post-Processing response...');
    this.sendProgress('Post-Processing response...');
    return this.#assistantMessageProcessor.process(message).
      then((processedMessage: ChatMessage) => {
        this.#addProcessedMessage(processedMessage);

        return processedMessage;
      });
  }

  clearMessages() {
    this.#messages = [];
    this.#processedMessages = [];
  }

  readFromFile(sessionId: string) {

    // Read a json file from the user's home directory containing the chat
    const fileName = `chat-${sessionId}.json`;
    const homeDirectory = os.homedir();
    const subDirectoryPath = path.join(homeDirectory, CHAT_DATA_DIRECTORY,
      sessionId);
    const filePath = path.join(subDirectoryPath, fileName);

    console.log('Reading file ' + filePath, sessionId);
    const chatData = JSON.parse(fs.readFileSync(filePath, {encoding: 'utf-8'}));
    this.#messages = chatData.messages;
    this.#processedMessages = chatData.processedMessages;
    this.#sessionId = chatData.sessionId;
    this.#isSubmitted = true;
  }

  #addMessage(message: ChatMessage) {
    this.#messages.push(message);
  }

  #addProcessedMessage(message: ChatMessage) {
    this.#processedMessages.push(message);
  }

  #createFileData() {
    return {
      sessionId: this.#sessionId,
      messages: this.#messages,
      processedMessages: this.#processedMessages,
    };
  }

  #writeToFile() {
    console.log('Writing file');
    // Write a json file to the user's home directory containing the history
    // entries
    const fileName = `chat-${this.#sessionId}.json`;
    const data = JSON.stringify(this.#createFileData(), null, 2);

    // Get the user's home directory
    const homeDirectory = os.homedir();

    // Set the subdirectory name and create the directory if it does not exist
    const subDirectoryPath = path.join(homeDirectory, CHAT_DATA_DIRECTORY,
      this.#sessionId);

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
export {ChatMessage, Role};
