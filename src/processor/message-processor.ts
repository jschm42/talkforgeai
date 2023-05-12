import MessageTransformer from './transformer/transformer';
import ChatMessage from '../service/to/chat-message';
import ChatSession from '@/service/to/chat-session';

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
  async process(message: ChatMessage, session: ChatSession) {
    console.log('PROCESS', message, session);
    const functions = this.transformers.map(
      (transformer) => transformer.process());

    // Chain the promises using reduce()
    const processedContent = await functions.reduce(
      (previousPromise, currentAsyncFunction) =>
        previousPromise.then((result: any) => currentAsyncFunction(result, session)),
      Promise.resolve(message.content),
    );

    return new ChatMessage(message.role, processedContent);
  }

}

export default MessageProcessor;
