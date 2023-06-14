package com.talkforgeai.talkforgeaiserver.openai;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OpenAIChatMessageRole {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant"),
    FUNCTION("function");

    private final String value;

    private OpenAIChatMessageRole(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }
}
