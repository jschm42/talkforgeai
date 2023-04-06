// See the Electron documentation for details on how to use preload scripts:
// https://www.electronjs.org/docs/latest/tutorial/process-model#preload-scripts

import {contextBridge, ipcMain, ipcRenderer} from 'electron';
import ChatIndexService, {IndexEntry} from './service/chat-index.service';

const chatIndexService = new ChatIndexService();

declare global {
  interface Window {
    chatIndexApi?: any,
    electronAPI?: any
  }
}

contextBridge.exposeInMainWorld('electronAPI', {
  sendToMainProcess: (channel: string, data: any) => {
    ipcRenderer.send(channel, data);
  },

  listenToMainProcess: (channel: string, callback: any) => {
    ipcRenderer.on(channel, (event, data) => {
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

