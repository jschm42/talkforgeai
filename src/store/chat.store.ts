import { reactive } from 'vue'
import {ChatMessage} from '../service/chat.service';
import {Persona} from '../service/persona.service';

const chatStore = reactive({
  sessionId: null,
  messages: [] as Array<ChatMessage>,
  processedMessages: [] as Array<ChatMessage>,
  persona: null,
});

export default chatStore;
