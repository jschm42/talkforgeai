package com.talkforgeai.talkforgeaiserver.service;

import java.util.List;

public record ChatGptResponse(
        String id,
        String object,
        Long created,
        String model,
        Usage usage,
        List<Choice> choices
) {
    public record Usage(
            int promptTokens,
            int completionTokens,
            int totalTokens
    ) {}
    public record Choice(
            Message message,
            String finishReason,
            int index
    ) {}

    public record Message(
            String role,
            String content
    ) {}

}
