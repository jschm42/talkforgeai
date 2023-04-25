// See the Electron documentation for details on how to use preload scripts:
// https://www.electronjs.org/docs/latest/tutorial/process-model#preload-scripts

import {contextBridge} from 'electron';
import ChatMessage from './service/to/chat-message';
import IndexEntry from './service/to/index-entry';
import ChatSession from './service/to/chat-session';

import AsssistantMessageProcessor from './processor/asssistant-message-processor';
import ChatService from './service/chat.service';
import ChatIndexService from './service/chat-index.service';
import ElevenlabsService, {VOICES} from './service/elevenlabs.service';
import ConfigService from './service/config.service';

const indexService = new ChatIndexService();
const chatService = new ChatService();
const elevenlabsService = new ElevenlabsService();
const configService = new ConfigService();
const assistantMessageProcessor = new AsssistantMessageProcessor();

declare global {
  interface Window {
    chatIndexAPI?: unknown,
    chatAPI?: unknown,
    configAPI?: unknown
  }
}

contextBridge.exposeInMainWorld('chatAPI', {
  process: async (message: ChatMessage) => {
    return assistantMessageProcessor.process(message);
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

contextBridge.exposeInMainWorld('configAPI', {
  getConfig: () => {
    return configService.getConfig();
  },
});
