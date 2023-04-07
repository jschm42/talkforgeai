// See the Electron documentation for details on how to use preload scripts:
// https://www.electronjs.org/docs/latest/tutorial/process-model#preload-scripts

import {contextBridge, ipcRenderer} from 'electron';
import ChatIndexService, {IndexEntry} from './service/chat-index.service';
import {ChatMessage} from './service/chat.service';

const chatIndexService = new ChatIndexService();

declare global {
  interface Window {
    chatIndexAPI?: unknown,
    chatAPI?: unknown
  }
}

contextBridge.exposeInMainWorld('chatAPI', {
  submitPrompt: (data: ChatMessage) => {
    ipcRenderer.send('submit-prompt', data);
  },

  listenToPromptReply: (callback: (t: ChatMessage) => void) => {
    ipcRenderer.on('submit-prompt-reply', (event, data) => {
      callback(data);
    });
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
    ipcRenderer.on('index-load-reply', (event, data) => {
      callback(data);
    });
  },
});

