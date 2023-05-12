// See the Electron documentation for details on how to use preload scripts:
// https://www.electronjs.org/docs/latest/tutorial/process-model#preload-scripts

import {contextBridge} from 'electron';
import ChatMessage from './service/to/chat-message';
import IndexEntry from './service/to/index-entry';
import ChatSession from './service/to/chat-session';

import AsssistantMessageProcessor from './processor/asssistant-message-processor';
import ImagePromptDownloadTransformer from './processor/transformer/image-prompt-download.transformer';
import ChatService from './service/chat.service';
import ChatIndexService from './service/chat-index.service';
import ElevenlabsService from './service/elevenlabs.service';
import ConfigService from './service/config.service';
import PersonaService from './service/persona.service';
import Persona, {ElevenLabsProperties} from './service/to/persona';
import os from 'os';
import path from 'path';
import {PERSONA_DIRECTORY} from './path-constants';

const indexService = new ChatIndexService();
const chatService = new ChatService();
const elevenlabsService = new ElevenlabsService();
const configService = new ConfigService();
const personaService = new PersonaService();
const assistantMessageProcessor = new AsssistantMessageProcessor();
const userMessageProcessor = new AsssistantMessageProcessor();
const imagePromptDownloadTransformer = new ImagePromptDownloadTransformer();

declare global {
  interface Window {
    chatIndexAPI?: unknown,
    chatAPI?: unknown,
    configAPI?: unknown,
    personaAPI?: unknown,
    transformerAPI?: unknown
  }
}

contextBridge.exposeInMainWorld('chatAPI', {
  processAssistantMessage: async (message: ChatMessage, session: ChatSession) => {
    return assistantMessageProcessor.process(message, session);
  },
  processUserMessage: async (message: ChatMessage, session: ChatSession) => {
    return userMessageProcessor.process(message, session);
  },
  loadChatSession: (chatSessionId: string) => {
    return chatService.readFromFile(chatSessionId);
  },
  writeChatSession: (chatSession: ChatSession) => {
    chatService.writeToFile(chatSession);
  },
  textToSpeech: async (text: string, properties: ElevenLabsProperties) => {
    console.log('Text to speech', text);

    let submitProperties = new ElevenLabsProperties();

    if (properties) {
      submitProperties = properties;
    }
    return await elevenlabsService.speachStream(text, submitProperties);
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
  getPersonaImagePath: (imagePath: string) => {
    return path.join(os.homedir(), PERSONA_DIRECTORY, imagePath);
  },
});

contextBridge.exposeInMainWorld('personaAPI', {
  getPersona: (name: string) => {
    return personaService.getPersonaByName(name);
  },
  getSystemMessagesForPersona: (persona: Persona) => {
    return personaService.getSystemMessagesForPersona(persona);
  },
  readPersonas: async () => {
    return await personaService.readPersonas();
  },

});

contextBridge.exposeInMainWorld('transformerAPI', {
  processAssistantMessage: async (message: ChatMessage, session: ChatSession) => {
    return await assistantMessageProcessor.process(message, session);
  },
  transformImage: async (content: string, session: ChatSession) => {
    const fn = imagePromptDownloadTransformer.process();
    return await fn(content, session);
  },
});
