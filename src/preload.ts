// See the Electron documentation for details on how to use preload scripts:
// https://www.electronjs.org/docs/latest/tutorial/process-model#preload-scripts

import {contextBridge} from 'electron';
import ChatIndexService from './service/chat-index.service';
import ChatService from './service/chat.service';

const chatIndexService = new ChatIndexService();
const chatService = new ChatService();

contextBridge.exposeInMainWorld('electronAPI', {

  chatIndex: chatIndexService,
  chat: chatService,

  loadIndex: () => {
    const service = new ChatIndexService();
    service.read();
    return service.index;
  },
});
