// See the Electron documentation for details on how to use preload scripts:
// https://www.electronjs.org/docs/latest/tutorial/process-model#preload-scripts

import {contextBridge} from 'electron';
import ChatMessage from './service/to/chat-message';
import IndexEntry from './service/to/index-entry';
import ChatSession from './service/to/chat-session';

import ChatService from './service/chat.service';
import ChatIndexService from './service/chat-index.service';
import ElevenlabsService, {VOICES} from './service/elevenlabs.service';
import OpenAiService from './service/openai.service';
import Role from './service/to/role';
import * as util from 'util';

const indexService = new ChatIndexService();
const chatService = new ChatService();
const elevenlabsService = new ElevenlabsService();

declare global {
  interface Window {
    chatIndexAPI?: unknown,
    chatAPI?: unknown
  }
}

contextBridge.exposeInMainWorld('chatAPI', {
  submitPrompt: async (prompt: string, previousMessages: Array<ChatMessage>) => {
    const newChatService = new ChatService();

    const preProcessedUserMessage = await newChatService.preProcess(prompt);
    console.log('Pre-processed', preProcessedUserMessage);

    return newChatService.submit(preProcessedUserMessage, previousMessages);
  },
  submitStreamTest: async () => {
    const openAiService = new OpenAiService();

    const delayTime = 10; // milliseconds
    const maxResponseLength = 200;
    const sleep = util.promisify(setTimeout);

    const startTime = Date.now();

    const messages = [
      new ChatMessage(Role.USER, 'Tell a funny joke.'),
    ];

    console.log('Requesting stream...');
    const response = await openAiService.chatCompletion(messages, true);

    const reader = response.body.getReader();

    reader.read().then(function processText({done, value}) {
      const str = new TextDecoder().decode(value);
      console.log('VALUE', str);

      if (done) {
        console.log('Stream complete');
        return;
      }

      return reader.read().then(processText);
    });

  },

  loadChatSession: (chatSessionId: string) => {
    return chatService.readFromFile(chatSessionId);
  },
  writeChatSession: (chatSession: ChatSession) => {
    chatService.writeToFile(chatSession);
  },
  textToSpeech: async (text: string) => {
    console.log('Text to speech', text);
    return await elevenlabsService.speachStream(text, VOICES.Elli);
  },
});

contextBridge.exposeInMainWorld('chatIndexAPI', {
  load: () => {
    return indexService.read();
  },
  save: (indexEntries: Array<IndexEntry>) => {
    indexService.write(indexEntries);
  },
});

