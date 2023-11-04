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

import StringUtil from '@/util/string-util';
import ChatMessage from '@/store/to/chat-message';
import Persona from '@/store/to/persona';

class Session {
  id: string;
  title: string;
  description: string;
  createdAt: Date | undefined;
  chatMessages: ChatMessage[] = [];
  persona: Persona | undefined;

  constructor(id: string, title: string, description: string) {
    this.title = StringUtil.truncateString(title, 50);
    this.description = description;
    this.id = id;
  }

}

export default Session;
