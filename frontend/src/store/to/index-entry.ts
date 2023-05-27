import StringUtil from '@/util/string-util';

class IndexEntry {
  readonly sessionId: string;
  title: string;
  description: string;
  timestamp: Date;

  constructor(sessionId: string, title: string, description: string, timestamp: Date) {
    this.title = StringUtil.truncateString(title, 50);
    this.description = description;
    this.timestamp = timestamp;
    this.sessionId = sessionId;
  }

}

export default IndexEntry;
