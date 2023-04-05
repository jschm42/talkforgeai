import MessageProcessor from './message-processor';

class UserMessageProcessor extends MessageProcessor {
  constructor() {
    super();
    this.addProcessor(new UrlTransformer());
  }
}