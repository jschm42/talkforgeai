<template>
  <ChatHeader></ChatHeader>
  <div ref="entries" class="flex-fill vertical-scrollbar no-horizontal-scrollbar">

    <ChatMessage v-for="(message, index) in store.messages" ref="chatMessageRef"
                 v-bind:key="index" :message="message" :messageIndex="index"></ChatMessage>

  </div>
  <!-- Input Section -->

  <ChatControl @submit-submitResult-received="submitResultReceived"></ChatControl>

</template>

<script>
import ChatControl from './ChatControl.vue';
import ChatMessage from './ChatMessage.vue';
import ChatHeader from './ChatHeader.vue';
import {useChatStore} from '@/store/chat-store';
import {Client} from '@stomp/stompjs';
import hljs from 'highlight.js';

export default {
  name: 'ChatContainer',
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function

    return {
      store,
    };
  },
  data() {
    return {};
  },
  components: {ChatHeader, ChatControl, ChatMessage},
  methods: {
    submitResultReceived() {
      console.log('Submit Result Received');
      // Scroll to bottom
      this.$refs.entries.scrollTop = this.$refs.entries.scrollHeight;

      if (this.store.autoSpeak) {
        console.log('Auto speaking last Chat-Message.');
        const lastChatMessage = this.$refs.chatMessageRef.slice(-1)[0];
        lastChatMessage.playAudio();
      }
    },
  },
  updated() {
    console.log('Chat-Component updated');
  },
  mounted() {

    const wsClient = new Client({
      brokerURL: `ws://localhost:8090/ws`,
      // connectHeaders: {
      //   login: "user",
      //   passcode: "password"
      // }
      debug: msg => {
        console.log('WS: ', msg);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    wsClient.onConnect = frame => {
      console.log('WS on connect');
      // Do something, all subscribes must be done is this callback
      // This is needed because this will be executed after a (re)connect
      const subscription = wsClient.subscribe('/topic/messages', message => {
        // called when the client receives a STOMP message from the server
        if (message.body) {
          const data = JSON.parse(message.body);
          console.log('WS received data' + message.body);

          if (data.type === 'RESPONSE') {
            console.log('WS Response-Message ', data.message);
            this.store.messages = [...this.store.messages, data.message];

            this.$nextTick(() => {
              hljs.highlightAll();
            });
          } else if (data.type === 'FUNCTION_CALL') {
            console.log('WS Function-Message ', data.message);
            this.store.messages = [...this.store.messages, data.message];
            this.store.sendFunctionConfirm(data.sessionId);
          } else if (data.type === 'STATUS') {
            console.log('WS Status-Message ', data.status);
            this.store.updateStatus(data.sessionId, data.status);
          } else {
            console.log('Unknown message type.');
          }
        } else {
          console.log('got empty message');
        }
      });
    };
    wsClient.onStompError = frame => {
      // Will be invoked in case of error encountered at Broker
      // Bad login/passcode typically will cause an error
      // Complaint brokers will set `message` header with a brief message. Body may contain details.
      // Compliant brokers will terminate the connection after any error
      console.log('Broker reported error: ' + frame.headers['message']);
      console.log('Additional details: ' + frame.body);
    };
    wsClient.activate();
  },

};
</script>

<style scoped>

</style>
