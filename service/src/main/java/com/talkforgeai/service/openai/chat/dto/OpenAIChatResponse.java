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

package com.talkforgeai.service.openai.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Date;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenAIChatResponse(String id,
                                 String object,
                                 Date created,
                                 String model,
                                 List<ResponseChoice> choices,
                                 ResponseUsage usage) {

  public record ResponseChoice(Integer index,
                               OpenAIChatMessage message,
                               @JsonProperty("finish_reason") FinishReason finishReason) {

    public enum FinishReason {
      STOP("stop"),
      FUNCTION_CALL("function_call");

      final String value;

      FinishReason(String value) {
        this.value = value;
      }

      @JsonValue
      public String getValue() {
        return value;
      }
    }

  }

  public record ResponseUsage(@JsonProperty("prompt_tokens") int promptTokens,
                              @JsonProperty("completion_tokens") int completionTokens,
                              @JsonProperty("total_tokens") int totalTokens) {

  }
}
