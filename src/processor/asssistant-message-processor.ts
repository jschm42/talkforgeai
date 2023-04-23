import MessageProcessor from './message-processor';
import CodeBlockTransformer from './transformer/code-block.transformer';
import CodeWordTransformer from './transformer/code-word.transformer';
import ImagePromptTransformer from './transformer/image-prompt.transformer';
import EndOfLineTransformer from './transformer/end-of-line.transformer';

/**
 * @extends MessageProcessor
 */
class AssistantMessageProcessor extends MessageProcessor {
  constructor() {
    super();
    this.addProcessor(new CodeBlockTransformer());
    this.addProcessor(new CodeWordTransformer());
    this.addProcessor(new EndOfLineTransformer());
    this.addProcessor(new ImagePromptTransformer());
  }
}

export default AssistantMessageProcessor;
