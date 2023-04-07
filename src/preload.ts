// See the Electron documentation for details on how to use preload scripts:
// https://www.electronjs.org/docs/latest/tutorial/process-model#preload-scripts

import {contextBridge, ipcRenderer} from 'electron';
import ChatMessage from './service/to/chat-message';
import IndexEntry from './service/to/index-entry';

declare global {
  interface Window {
    chatIndexAPI?: unknown,
    chatAPI?: unknown
  }
}

contextBridge.exposeInMainWorld('chatAPI', {
  submitPrompt: (prompt: string) => {
    ipcRenderer.send('submit-prompt', prompt);
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

