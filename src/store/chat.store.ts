import {reactive} from 'vue';
import ChatMessage from '../service/to/chat-message';
import Persona from '../service/to/persona';
import Role from '../service/to/role';

const chatStore = reactive({
  sessionId: '' as string,
  messages: [] as Array<ChatMessage>,
  processedMessages: [] as Array<ChatMessage>,
  persona: Persona,
  submit(prompt: string) {
    console.log('submit', prompt);

    // @ts-ignore
    window.chatAPI.listenToPromptReply(({originalResponseMessage, processedResponseMessage}) => {
      this.messages.push(originalResponseMessage);
      this.processedMessages.push(processedResponseMessage);
      console.log('Reply received', originalResponseMessage, processedResponseMessage);
    });

    const previousMessages = [];
    for (let i = 0; i < this.messages.length; i++) {
      if (this.messages[i].role === Role.USER) {
        previousMessages.push(Object.assign({}, this.processedMessages[i]));
      } else {
        previousMessages.push(Object.assign({}, this.messages[i]));
      }
    }

    this.messages.push(new ChatMessage(Role.USER, prompt));

    // @ts-ignore
    window.chatAPI.submitPrompt(prompt, previousMessages);
  },
});

export default chatStore;
