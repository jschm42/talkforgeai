package com.talkforgeai.service.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openai")
public record OpenAIProperties(String apiKey,
                               String chatUrl,
                               String imageUrl,
                               String postmanApiKey,
                               String postmanChatUrl,
                               String postmanRequestId,
                               boolean usePostman) {
}
