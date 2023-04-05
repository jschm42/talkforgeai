import MessageTransformer from './transformer';

class EndOfLineTransformer extends MessageTransformer {
  /**
   * @returns {Function<Promise<string>>}
   */
  process() {
    return (content: string) => new Promise((resolve, reject) => {
      this.sendProgress('Processing end of line...');
      resolve(content.replaceAll('\n\n', '<p />').replaceAll('\n', '<br />'));
    });
    //return content.replaceAll('\n\n', '<p />').replaceAll('\n', '<br />');
  }

}

export default EndOfLineTransformer;
