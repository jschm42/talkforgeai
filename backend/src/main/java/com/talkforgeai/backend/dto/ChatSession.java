package com.talkforgeai.backend.dto;


import com.talkforgeai.service.openai.dto.OpenAIChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record ChatSession(
        UUID sessionId,
        List<OpenAIChatMessage> systemMessages,
        List<OpenAIChatMessage> messages,
        List<OpenAIChatMessage> processedMessages) {

    public ChatSession(UUID sessionId) {
        this(sessionId, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

}
