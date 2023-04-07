import OpenAiService from './openai.service';
import UserMessageProcessor from '../processor/user-message-processor';
import AssistantMessageProcessor from '../processor/asssistant-message-processor';
import ChatMessage from './to/chat-message';
import Role from './to/role';

class NewChatService {

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

  submit(promptMessage: ChatMessage, pastMessages: Array<ChatMessage>) {
    const requestMessages = pastMessages.concat(promptMessage);

    return this.createRequestPromise(requestMessages).then((message: ChatMessage) => {
      return this.createPostProcessPromise(message);
    }).then((processed) => {
      //this.#writeToFile();
      return processed;
    });
  }

  createRequestPromise(messageRequest: Array<ChatMessage>) {
    console.log('Requesting response...');
    return this.#openAiService.chatCompletion(messageRequest).then((response: any) => {
      console.log('Response: ', response);

      const chatMessage = new ChatMessage(Role.ASSISTANT, response.content);
      return chatMessage;
    });
  }

  createPostProcessPromise(originalMessage: ChatMessage) {
    console.log('Post-Processing response...');
    return this.#assistantMessageProcessor.process(originalMessage).
      then((processedMessage: ChatMessage) => {
        return {originalMessage, processedMessage};
      });
  }

  //
  // readFromFile(sessionId: string) {
  //
  //   // Read a json file from the user's home directory containing the chat
  //   const fileName = `chat-${sessionId}.json`;
  //   const homeDirectory = os.homedir();
  //   const subDirectoryPath = path.join(homeDirectory, CHAT_DATA_DIRECTORY,
  //     sessionId);
  //   const filePath = path.join(subDirectoryPath, fileName);
  //
  //   console.log('Reading file ' + filePath, sessionId);
  //   const chatData = JSON.parse(fs.readFileSync(filePath, {encoding: 'utf-8'}));
  //   this.#messages = chatData.messages;
  //   this.#processedMessages = chatData.processedMessages;
  //   this.#sessionId = chatData.sessionId;
  //   this.#isSubmitted = true;
  // }
  //
  // #createFileData() {
  //   return {
  //     sessionId: this.#sessionId,
  //     messages: this.#messages,
  //     processedMessages: this.#processedMessages,
  //   };
  // }
  //
  // #writeToFile() {
  //   console.log('Writing file');
  //   // Write a json file to the user's home directory containing the history
  //   // entries
  //   const fileName = `chat-${this.#sessionId}.json`;
  //   const data = JSON.stringify(this.#createFileData(), null, 2);
  //
  //   // Get the user's home directory
  //   const homeDirectory = os.homedir();
  //
  //   // Set the subdirectory name and create the directory if it does not exist
  //   const subDirectoryPath = path.join(homeDirectory, CHAT_DATA_DIRECTORY,
  //     this.#sessionId);
  //
  //   fs.mkdirSync(subDirectoryPath, {recursive: true});
  //
  //   // Set the file path and name
  //   const filePath = path.join(subDirectoryPath, fileName);
  //
  //   try {
  //     fs.writeFileSync(filePath, data, {encoding: 'utf-8'});
  //   } catch (error) {
  //     console.log('Error writing file', error);
  //   }
  // }
}

export default NewChatService;
