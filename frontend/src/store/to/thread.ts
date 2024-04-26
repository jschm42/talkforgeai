/*
 * Copyright (c) 2023-2024 Jean Schmitz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import Role from '@/store/to/role';

class Thread {
  id = '';
  title = '';
  createdAt: Date | null = null;
}

class ThreadMessage {
  id = '';
  createdAt: Date | null = null;
  threadId: string | null = null;
  assistantId: string | null = null;
  role: Role | undefined;
  content: string | null = null;

  constructor(id: string, role: Role, content: string, assistantId: string) {
    this.id = id;
    this.role = role;
    this.content = content;
    this.assistantId = assistantId;
  }
}

class ThreadMessageList {
  object: string | null = null;
  data: Array<ThreadMessage> | null = null;
  first_id: string | null = null;
  last_id: string | null = null;
  next_id: string | null = null;
}

class TreadMessageListParsed {
  messageList: ThreadMessageList | null = null;
  parsedMessages: Array<ParsedThreadMessage> | null = null;
}

class ParsedThreadMessage {
  parsedContent: string | null = null;
  message: ThreadMessage | null = null;
}

export default Thread;
export {
  ThreadMessage,
  ParsedThreadMessage,
  ThreadMessageList,
  TreadMessageListParsed,
};
