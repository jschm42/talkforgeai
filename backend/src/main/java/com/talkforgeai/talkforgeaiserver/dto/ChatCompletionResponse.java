package com.talkforgeai.talkforgeaiserver.dto;

import com.talkforgeai.talkforgeaiserver.openai.dto.OpenAIChatMessage;

import java.util.List;

public record ChatCompletionResponse(String sessionId,
                                     List<OpenAIChatMessage> processedMessages) {
}
