package com.talkforgeai.talkforgeaiserver.dto;

import com.talkforgeai.talkforgeaiserver.openai.dto.OpenAIChatMessage;

import java.util.List;
import java.util.UUID;

public record ChatCompletionResponse(UUID sessionId,
                                     List<OpenAIChatMessage> processedMessages) {
}
