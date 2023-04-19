// See the Electron documentation for details on how to use preload scripts:
// https://www.electronjs.org/docs/latest/tutorial/process-model#preload-scripts

import {contextBridge} from 'electron';
import ChatMessage from './service/to/chat-message';
import IndexEntry from './service/to/index-entry';
import ChatSession from './service/to/chat-session';

import ChatService from './service/chat.service';
import ChatIndexService from './service/chat-index.service';

const indexService = new ChatIndexService();
const chatService = new ChatService();

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
  loadChatSession: (chatSessionId: string) => {
    //ipcRenderer.send('load-chat-session', chatSessionId);
    return chatService.readFromFile(chatSessionId);
  },
  writeChatSession: (chatSession: ChatSession) => {
    //ipcRenderer.send('write-chat-session', chatSession);

    chatService.writeToFile(chatSession);
  },

});

contextBridge.exposeInMainWorld('chatIndexAPI', {
  load: () => {
    //ipcRenderer.send('load-index');
    return indexService.read();
  },
  save: (indexEntries: Array<IndexEntry>) => {
    //ipcRenderer.send('save-index', indexEntries);
    indexService.write(indexEntries);
  },
});

