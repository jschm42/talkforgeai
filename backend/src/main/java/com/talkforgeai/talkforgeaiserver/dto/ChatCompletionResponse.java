package com.talkforgeai.talkforgeaiserver.dto;

import com.talkforgeai.talkforgeaiserver.openai.OpenAIChatMessage;

import java.util.List;

public record ChatCompletionResponse(String sessionId,
                                     List<OpenAIChatMessage> processedMessages) {
}
