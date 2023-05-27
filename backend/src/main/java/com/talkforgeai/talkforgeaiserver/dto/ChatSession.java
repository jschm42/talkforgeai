package com.talkforgeai.talkforgeaiserver.dto;

import com.theokanning.openai.completion.chat.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public record ChatSession(
        String sessionId,
        List<ChatMessage> systemMessages,
        List<ChatMessage> messages,
        List<ChatMessage> processedMessages) {

    public ChatSession(String sessionId) {
        this(sessionId, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

}
