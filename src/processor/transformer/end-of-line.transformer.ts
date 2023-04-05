import MessageTransformer from './transformer';

class EndOfLineTransformer extends MessageTransformer {
  /**
   * @returns {Function<Promise<string>>}
   */
  process() {
    return (content: string) => new Promise((resolve, reject) => {
      this.sendProgress('Processing end of line...');
      resolve(content.replace(/\n\n/g, '<p />').replace(/\n/g, '<br />'));
    });
  }

}

export default EndOfLineTransformer;
