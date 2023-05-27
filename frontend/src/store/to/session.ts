import StringUtil from '@/util/string-util';

class Session {
  id: string;
  title: string;
  description: string;

  constructor(id: string, title: string, description: string) {
    this.title = StringUtil.truncateString(title, 50);
    this.description = description;
    this.id = id;
  }

}

export default Session;
