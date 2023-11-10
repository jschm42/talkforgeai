/*
 * Copyright (c) 2023 Jean Schmitz.
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

class Thread {
  id = '';
  title = '';
  created_at: Date | null = null;
  metadata: any;
}

class Content {
  type = '';
  text: TextContent | null = null;
}

class TextContent {
  value: string | null | undefined;
  annotations: [] | null = null;
}

class ThreadMessage {
  id = '';
  created_at: Date | null = null;
  thread_id: string | null = null;
  file_ids: [] | null = null;
  assistant_id: string | null = null;
  run_id: string | null = null;
  role: 'user' | 'assistant' | undefined;
  content: Array<Content> | null = null;
  metadata: any;
}

class ThreadMessageList {
  object: string | null = null;
  data: Array<ThreadMessage> | null = null;
  first_id: string | null = null;
  last_id: string | null = null;
  next_id: string | null = null;
}

class TreadMessageListParsed {
  message_list: ThreadMessageList | null = null;
  parsed_messages: Array<ParsedThreadMessage> | null = null;
}

class ParsedThreadMessage {
  parsed_content: string | null = null;
  message: ThreadMessage | null = null;
}

export default Thread;
export {ThreadMessage, Content, TextContent, ParsedThreadMessage, ThreadMessageList, TreadMessageListParsed};
