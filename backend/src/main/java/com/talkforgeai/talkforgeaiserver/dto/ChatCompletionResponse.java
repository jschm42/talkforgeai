package com.talkforgeai.talkforgeaiserver.dto;

import com.theokanning.openai.completion.chat.ChatMessage;

import java.util.List;

public record ChatCompletionResponse(String sessionId,
                                     List<ChatMessage> processedMessages) {
}
