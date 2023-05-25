package com.talkforgeai.talkforgeaiserver.service;

import com.talkforgeai.talkforgeaiserver.dto.ChatGPTProperties;
import com.talkforgeai.talkforgeaiserver.dto.ChatMessage;

import java.util.List;

public record ChatGptRequest(
        String model,
        List<ChatMessage> messages,
        ChatGPTProperties properties
) {
        public ChatGptRequest(List<ChatMessage> messages) {
                this("gpt-3.5-turbo-0301", messages, new ChatGPTProperties());
        }

}