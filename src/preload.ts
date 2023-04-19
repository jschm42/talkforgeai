// See the Electron documentation for details on how to use preload scripts:
// https://www.electronjs.org/docs/latest/tutorial/process-model#preload-scripts

import {contextBridge, ipcRenderer} from 'electron';
import ChatMessage from './service/to/chat-message';
import IndexEntry from './service/to/index-entry';
import ChatSession from './service/to/chat-session';

import NewChatService from './service/new-chat.service';

declare global {
  interface Window {
    chatIndexAPI?: unknown,
    chatAPI?: unknown
  }
}

contextBridge.exposeInMainWorld('chatAPI', {
  submitPrompt: async (prompt: string, previousMessages: Array<ChatMessage>) => {
    const newChatService = new NewChatService();

    const preProcessedUserMessage = await newChatService.preProcess(prompt);
    console.log('Pre-processed', preProcessedUserMessage);

    return newChatService.submit(preProcessedUserMessage, previousMessages);
  },
  loadChatSession: (chatSessionId: string) => {
    ipcRenderer.send('load-chat-session', chatSessionId);
  },
  writeChatSession: (chatSession: ChatSession) => {
    //ipcRenderer.send('write-chat-session', chatSession);
    const newChatService = new NewChatService();
    newChatService.writeToFile(chatSession);
  },

});

contextBridge.exposeInMainWorld('chatIndexAPI', {
  load: () => {
    ipcRenderer.send('load-index');
  },
  save: (indexEntries: Array<IndexEntry>) => {
    ipcRenderer.send('save-index', indexEntries);
  },
  listenToLoadReply: (callback: (t: Array<IndexEntry>) => void) => {
    ipcRenderer.once('index-load-reply', (event, data) => {
      callback(data);
    });
  },
});

