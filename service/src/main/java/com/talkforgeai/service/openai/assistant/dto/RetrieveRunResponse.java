package com.talkforgeai.service.openai.assistant.dto;

import java.util.List;
import java.util.Map;

public record RetrieveRunResponse(
        String id,
        String object,
        long createdAt,
        String assistantId,
        String threadId,
        String status,
        Long startedAt,
        Long expiresAt,
        Long cancelledAt,
        Long failedAt,
        Long completedAt,
        String lastError,
        String model,
        String instructions,
        List<ToolRecord> tools,
        List<String> fileIds,
        Map<String, Object> metadata) {

    public record ToolRecord(
            String type
    ) {
    }
}
