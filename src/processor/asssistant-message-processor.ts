import MessageProcessor from './message-processor';
import ImagePromptDownloadTransformer from './transformer/image-prompt-download.transformer';

/**
 * @extends MessageProcessor
 */
class AssistantMessageProcessor extends MessageProcessor {
  constructor() {
    super();
    //this.addProcessor(new CodeBlockTransformer());
    //this.addProcessor(new CodeWordTransformer());
    //this.addProcessor(new EndOfLineTransformer());
    this.addProcessor(new ImagePromptDownloadTransformer());
  }
}

export default AssistantMessageProcessor;
