import MessageProcessor from './message-processor';
import ImagePromptTransformer from './transformer/image-prompt.transformer';

/**
 * @extends MessageProcessor
 */
class AssistantMessageProcessor extends MessageProcessor {
  constructor() {
    super();
    //this.addProcessor(new CodeBlockTransformer());
    //this.addProcessor(new CodeWordTransformer());
    //this.addProcessor(new EndOfLineTransformer());
    this.addProcessor(new ImagePromptTransformer());
  }
}

export default AssistantMessageProcessor;
