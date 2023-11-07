package com.talkforgeai.service.openai.assistant.dto;

import java.util.List;
import java.util.Map;

public record PostMessageResponse(
        String id,
        String object,
        long createdAt,
        String threadId,
        String role,
        List<ContentRecord> content,
        List<String> fileIds,
        String assistantId,
        String runId,
        Map<String, Object> metadata) {

    public record ContentRecord(
            String type,
            TextRecord text
    ) {

        public record TextRecord(
                String value,
                List<Object> annotations
        ) {
        }
    }
}

