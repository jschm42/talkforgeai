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

package com.talkforgeai.service.openai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

//@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpenAIChatMessage(
        Role role,
        String content,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        String name,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("function_call")
        FunctionCall functionCall) {

    public OpenAIChatMessage(Role role, String content) {
        this(role, content, null, null);
    }

    public OpenAIChatMessage(Role role, String content, String name) {
        this(role, content, name, null);
    }

    public OpenAIChatMessage(Role role, String content, FunctionCall functionCall) {
        this(role, content, null, functionCall);
    }

    public OpenAIChatMessage(FunctionCall functionCall) {
        this(Role.FUNCTION, null, null, functionCall);
    }

    public enum Role {
        SYSTEM("system"),
        USER("user"),
        ASSISTANT("assistant"),
        FUNCTION("function");

        private final String value;

        private Role(String value) {
            this.value = value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }
    }


    public record FunctionCall(

            String name,
            String arguments) {
    }

}
