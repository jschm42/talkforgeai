import MessageProcessor from './message-processor';
import UrlTransformer from './transformer/url.transformer';

class UserMessageProcessor extends MessageProcessor {
  constructor() {
    super();
    this.addProcessor(new UrlTransformer());
  }
}

export default UserMessageProcessor;
