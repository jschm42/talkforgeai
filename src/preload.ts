// See the Electron documentation for details on how to use preload scripts:
// https://www.electronjs.org/docs/latest/tutorial/process-model#preload-scripts

import {contextBridge} from 'electron';
import ChatIndexService, {IndexEntry} from './service/chat-index.service';
import ChatService from './service/chat.service';

const chatIndexService = new ChatIndexService();
const chatService = new ChatService();

declare global {
  interface Window {
    chatIndexApi?: any;
    chatApi?: any;
  }
}

contextBridge.exposeInMainWorld('chatIndexApi', {
  load: () => {
    return chatIndexService.read();
  },
  save: (indexEntries: Array<IndexEntry>) => {
    chatIndexService.write(indexEntries);
  }
});

contextBridge.exposeInMainWorld('chatApi', {
  load: (sessionId: string) => {
    return chatService.readFromFile(sessionId);
  },
});



