<template>
  <!--    <nav>-->
  <!--        <router-link to="/">Home</router-link>-->
  <!--        |-->
  <!--    </nav>-->
  <router-view/>
</template>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}
</style>


<script>
import {useChatStore} from '@/store/chat-store';
import {Client} from '@stomp/stompjs';

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
          console.log('got message with body ' + message.body);
          const data = JSON.parse(message.body);
          this.store.updateStatus(data.sessionId, data.message);
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
