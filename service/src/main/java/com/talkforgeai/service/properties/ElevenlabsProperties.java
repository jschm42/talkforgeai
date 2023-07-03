package com.talkforgeai.service.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "elevenlabs")
public record ElevenlabsProperties(String apiKey, String apiUrl) {

}
