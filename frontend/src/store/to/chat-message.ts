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

import Role from './role';

class FunctionCall {
  name: string | undefined;
  arguments: string | undefined;
}

class ChatMessage {

  role: Role;

  content: string;

  name: string | undefined;

  function_call: FunctionCall | undefined;

  constructor(role: Role, content: string, name?: string, function_call?: FunctionCall) {
    this.role = role;
    this.content = content;
    this.name = name;
    this.function_call = function_call;
  }
}

export default ChatMessage;
export {FunctionCall};
