package com.talkforgeai.talkforgeaiserver.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openai")
public record OpenAIProperties(String apiKey, String chatUrl, String imageUrl) {

}
