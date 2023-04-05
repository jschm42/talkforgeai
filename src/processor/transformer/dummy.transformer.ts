import MessageTransformer from './transformer';

class DummyTransformer extends MessageTransformer {
  process() {
    return (content: string) => new Promise((resolve, reject) => {
      console.log('DummyTransformer.process', content);
      resolve('X' + content);
    });

  }
}

export default DummyTransformer;
