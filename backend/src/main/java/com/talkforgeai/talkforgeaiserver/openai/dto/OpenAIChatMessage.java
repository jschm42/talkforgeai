package com.talkforgeai.talkforgeaiserver.openai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

//@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpenAIChatMessage(
        Role role,
        String content,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("function_call")
        FunctionCall functionCall) {

    public OpenAIChatMessage(Role role, String content) {
        this(role, content, null);
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
