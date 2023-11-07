package com.talkforgeai.service.openai.assistant.dto;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;

public record RunConversationResponse(
        String id,
        String object,
        long createdAt,
        String assistantId,
        String threadId,
        String status,
        OptionalLong startedAt,
        long expiresAt,
        OptionalLong cancelledAt,
        OptionalLong failedAt,
        OptionalLong completedAt,
        Optional<String> lastError,
        String model,
        String instructions,
        List<ToolRecord> tools,
        List<String> fileIds,
        Map<String, Object> metadata
) {
    public record ToolRecord(
            String type
    ) {
    }

}
