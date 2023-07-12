const STREAM_REGEX = /{"content":(.*?)},/;
const QUOTE_REGEX = /\\"/;

class ChatStreamService {

  async streamSubmit(
    sessionId: string, content: string, messageCallback: (content: string[], isDone: boolean) => void) {
    const response = await fetch('http://localhost:8090/api/v1/chat/stream/submit', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },

      //make sure to serialize your JSON body
      body: JSON.stringify({sessionId, content}),
    });

    if (!response.body) {
      console.error('ReadableStream not yet supported in this browser.');
      return;
    }

    const reader = response.body.getReader();

    const decoder = new TextDecoder();
    let done = false;

    while (!done) {
      const row = await reader.read();
      console.log('ROW: ', row);

      const data = decoder.decode(row.value, {stream: true});
      console.log('DATA: ', data);
      //const json = JSON.parse(data);
      const content = this.parseStreamResponse(data);

      console.log('PARSED: ', content);
      done = row.done;

      messageCallback(content, done);
    }

    console.log('Stream complete');

  }

  parseStreamResponse(data: string) {
    // FIXME Does not parse the content corretly, if a " is inside
    // return data.split('\n\n').
    //   filter(e => e.length > 0).
    //   map(e => STREAM_REGEX.exec(e)).
    //   filter(e => e != null).
    //   map(p => {
    //     return p == null ? '' : p[1];
    //   });
    return data.split('\n\n').map(d => d.substring(5));
  }

}

export default ChatStreamService;

