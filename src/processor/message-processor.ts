import MessageTransformer from './transformer/transformer';
import ChatMessage from '../service/to/chat-message';

class MessageProcessor {

  /**
   * @member {Array<MessageTransformer>}
   */
  transformers: Array<MessageTransformer> = [];

  addProcessor(transformer: MessageTransformer) {
    this.transformers.push(transformer);
  }

  /**
   * Process the message
   * @param {ChatMessage} message
   * @returns {Promise<ChatMessage>}
   */
  async process(message: ChatMessage) {
    const functions = this.transformers.map(
      (transformer) => transformer.process());

    // Chain the promises using reduce()
    const processedContent = await functions.reduce(
      (previousPromise, currentAsyncFunction) =>
        previousPromise.then((result: any) => currentAsyncFunction(result)),
      Promise.resolve(message.content),
    );

    return new ChatMessage(message.role, processedContent);
  }

}

export default MessageProcessor;
