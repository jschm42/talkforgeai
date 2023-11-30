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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import java.util.Map;

public record OpenAIFunction(String name,
                             String description,
                             FunctionParameters parameters,
                             @JsonProperty("function_call") FunctionCall functionCall) {

  public enum FunctionCall {
    AUTO("auto"),
    NONE("none");

    private final String value;

    FunctionCall(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

  }

  public record FunctionParameters(ParameterType type,
                                   Map<String, FunctionProperty> properties,
                                   List<String> required) {

    public enum ParameterType {
      OBJECT("object"),
      STRING("string");

      private String value;

      ParameterType(String value) {
        this.value = value;
      }

      @JsonValue
      public String getValue() {
        return value;
      }
    }

    public record FunctionProperty(ParameterType type,
                                   String description) {

    }


  }

}
