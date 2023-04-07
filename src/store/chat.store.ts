import {reactive} from 'vue';
import ChatMessage from '../service/to/chat-message';
import Persona from '../service/to/persona';

const chatStore = reactive({
  sessionId: '' as string,
  messages: [] as Array<ChatMessage>,
  processedMessages: [] as Array<ChatMessage>,
  persona: Persona,
  submit(prompt: string) {
    console.log('submit', prompt);

    // @ts-ignore
    window.chatAPI.listenToPromptReply(({originalMessage, processedMessage}) => {
      this.messages.push(originalMessage);
      this.processedMessages.push(processedMessage);
      console.log('Reply received', originalMessage, processedMessage);
    });

    // @ts-ignore
    window.chatAPI.submitPrompt(prompt);
  },
});

export default chatStore;
