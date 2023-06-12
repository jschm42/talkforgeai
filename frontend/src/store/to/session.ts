import StringUtil from '@/util/string-util';
import ChatMessage from '@/store/to/chat-message';
import Persona from '@/store/to/persona';

class Session {
  id: string;
  title: string;
  description: string;
  chatMessages: ChatMessage[] = [];
  persona: Persona | undefined;

  constructor(id: string, title: string, description: string) {
    this.title = StringUtil.truncateString(title, 50);
    this.description = description;
    this.id = id;
  }

}

export default Session;
