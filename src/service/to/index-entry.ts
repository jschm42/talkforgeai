class IndexEntry {
  sessionId: string;
  title: string;
  description: string;
  timestamp: Date;
  selected: boolean;

  constructor(
    sessionId: string, title: string, description: string, timestamp: Date) {
    this.title = title;
    this.description = description;
    this.timestamp = timestamp;
    this.sessionId = sessionId;
    this.selected = false;
  }
}

export default IndexEntry;
