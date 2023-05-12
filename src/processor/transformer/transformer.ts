class MessageTransformer {

  /**
   * abstract method
   * @abstract
   * @returns {Function<Promise<string>>} Promise that processes the content
   */
  process(): any {
    throw new Error('Not implemented');
  }

}

export default MessageTransformer;
