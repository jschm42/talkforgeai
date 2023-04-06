import {reactive} from 'vue';
import {ChatMessage} from '../service/chat.service';

const chatStore = reactive({
  sessionId: window.commonApi.chat.sessionId,
  messages: window.commonApi.chat.messages as Array<ChatMessage>,
  processedMessages: window.commonApi.chat.processedMessages as Array<ChatMessage>,
  persona: window.commonApi.chat.persona,
  submit(prompt: string) {
    console.log('submit', prompt);
    window.chatApi.submit(prompt).then((processedMessage: ChatMessage) => {
      console.log('processedMessage', processedMessage);
      //this.processedMessages.push(new ChatMessage(Role.USER, response.data.choices[0].text));
    });

  },
});

export default chatStore;
