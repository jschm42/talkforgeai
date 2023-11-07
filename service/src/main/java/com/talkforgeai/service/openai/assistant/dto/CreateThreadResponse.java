package com.talkforgeai.service.openai.assistant.dto;

import java.util.Map;

public record CreateThreadResponse(
        String id,
        String object,
        long createdAt,
        Map<String, Object> metadata
) {
}
