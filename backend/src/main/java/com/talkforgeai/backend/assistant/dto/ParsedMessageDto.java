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

package com.talkforgeai.backend.assistant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.theokanning.openai.messages.Message;

public class ParsedMessageDto {

  @JsonProperty("parsed_content")
  String parsedContent;

  Message message;

  public String getParsedContent() {
    return parsedContent;
  }

  public void setParsedContent(String parsedContent) {
    this.parsedContent = parsedContent;
  }

  public Message getMessage() {
    return message;
  }

  public void setMessage(Message message) {
    this.message = message;
  }
}
