// See the Electron documentation for details on how to use preload scripts:
// https://www.electronjs.org/docs/latest/tutorial/process-model#preload-scripts

import {contextBridge, ipcMain, ipcRenderer} from 'electron';
import ChatIndexService, {IndexEntry} from './service/chat-index.service';
import {ChatMessage} from './service/chat.service';

const chatIndexService = new ChatIndexService();

declare global {
  interface Window {
    chatIndexApi?: any,
    chatAPI?: unknown
  }
}

type ChatMessageCallback = (t: ChatMessage) => void;

contextBridge.exposeInMainWorld('chatAPI', {
  submitPrompt: (data: ChatMessage) => {
    ipcRenderer.send('submit-prompt', data);
  },

  listenToPromptReply: (callback: ChatMessageCallback) => {
    ipcRenderer.on('submit-prompt-reply', (event, data) => {
      callback(data);
    });
  },
});

contextBridge.exposeInMainWorld('chatIndexApi', {
  sendMessage(chanel: string, message: any) {
    ipcMain.emit(chanel, message);
  },

  load: () => {
    return chatIndexService.read();
  },
  save: (indexEntries: Array<IndexEntry>) => {
    chatIndexService.write(indexEntries);
  },
});

