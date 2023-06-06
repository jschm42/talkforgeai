package com.talkforgeai.talkforgeaiserver.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "elevenlabs")
public record ElevenlabsProperties(String apiKey, String apiUrl) {

}
