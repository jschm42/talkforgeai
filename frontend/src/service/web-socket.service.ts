import {Client, IFrame} from '@stomp/stompjs';
import {useChatStore} from '@/store/chat-store';

class WebSocketService {
  functionCallHandler: (data: any) => void;
  statusUpdateHandler: (data: any) => void;
  responseHandler: (data: any) => void;

  private store: any;

  constructor() {
    this.store = useChatStore(); // Call useMyStore() inside the setup function
    this.functionCallHandler = (data) => {
    };
    this.statusUpdateHandler = (data) => {
    };
    this.responseHandler = (data) => {
    };
  }

  createClient() {
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

    wsClient.onConnect = this.getOnConnect(wsClient);

    wsClient.onStompError = this.getOnStompError();

    return wsClient;
  }

  private getOnStompError() {
    return (frame: IFrame) => {
      // Will be invoked in case of error encountered at Broker
      // Bad login/passcode typically will cause an error
      // Complaint brokers will set `message` header with a brief message. Body may contain details.
      // Compliant brokers will terminate the connection after any error
      console.log('Broker reported error: ' + frame.headers['message']);
      console.log('Additional details: ' + frame.body);
    };
  }

  private getOnConnect(wsClient: Client) {
    return (frame: IFrame) => {
      console.log('WS on connect');
      // Do something, all subscribes must be done is this callback
      // This is needed because this will be executed after a (re)connect
      const subscription = wsClient.subscribe('/topic/messages', message => {
        // called when the client receives a STOMP message from the server

        if (message.body) {
          const data = JSON.parse(message.body);

          console.log('WS received data', data);

          if (data.sessionId !== this.store.sessionId) {
            console.log('Message not for this session id.');
            return;
          }

          if (data.type === 'RESPONSE') {
            console.log('WS Response-Message ', data.message);
            this.responseHandler(data);
          } else if (data.type === 'FUNCTION_CALL') {
            console.log('WS Function-Message ', data.message);
            this.functionCallHandler(data);
          } else if (data.type === 'STATUS') {
            console.log('WS Status-Message ', data.status);
            this.statusUpdateHandler(data);
          } else {
            console.log('Unknown message type.');
          }
        } else {
          console.log('got empty message');
        }
      });
    };
  }
}

export default WebSocketService;
