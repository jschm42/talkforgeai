package com.talkforgeai.backend.dto;

import com.theokanning.openai.completion.chat.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record ChatSession(
        UUID sessionId,
        List<ChatMessage> systemMessages,
        List<ChatMessage> messages,
        List<ChatMessage> processedMessages) {

    public ChatSession(UUID sessionId) {
        this(sessionId, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

}
