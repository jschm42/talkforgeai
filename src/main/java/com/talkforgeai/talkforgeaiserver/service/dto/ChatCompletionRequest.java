package com.talkforgeai.talkforgeaiserver.service.dto;

public record ChatCompletionRequest(
    String sessionId,

    String personaName,
    String prompt) {
}
