package com.talkforgeai.talkforgeaiserver.dto;

import com.talkforgeai.talkforgeaiserver.openai.dto.OpenAIChatMessage;

public record ChatCompletionResponse(String sessionId,
                                     OpenAIChatMessage processedMessage) {
}
