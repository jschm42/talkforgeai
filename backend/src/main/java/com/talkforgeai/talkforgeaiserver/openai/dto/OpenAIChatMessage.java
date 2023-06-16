package com.talkforgeai.talkforgeaiserver.openai.dto;

public class OpenAIChatMessage {
    OpenAIChatMessageRole role;
    String content;

    public OpenAIChatMessage() {
    }

    public OpenAIChatMessage(OpenAIChatMessageRole role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OpenAIChatMessageRole getRole() {
        return role;
    }

    public void setRole(OpenAIChatMessageRole role) {
        this.role = role;
    }
}
