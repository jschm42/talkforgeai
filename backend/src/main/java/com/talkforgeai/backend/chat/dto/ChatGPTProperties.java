package com.talkforgeai.backend.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChatGPTProperties(
        @JsonProperty("max_tokens")
        int maxTokens,
        double temperature,

        @JsonProperty("top_p")
        float topP,

        @JsonProperty("frequency_penalty")
        float frequencyPenalty,

        @JsonProperty("presence_penalty")
        float presencePenalty) {
    public ChatGPTProperties() {
        this(2048, 0.7f, 1.0f, 0.0f, 0.0f);
    }

}
