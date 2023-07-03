package com.talkforgeai.backend.chat.dto;

import com.talkforgeai.service.openai.dto.OpenAIChatMessage;

import java.util.List;
import java.util.UUID;

public record ChatCompletionResponse(UUID sessionId,
                                     List<OpenAIChatMessage> processedMessages) {
}
