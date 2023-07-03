package com.talkforgeai.service.elevenlabs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ElevenLabsApiRequest(String text,
                                   @JsonProperty("model_id")
                                   String modelId,
                                   @JsonProperty("voice_settings")
                                   VoiceSettings voiceSettings) {

    public record VoiceSettings(double stability,
                                @JsonProperty("similarity_boost")
                                double similarityBoost) {

        public VoiceSettings() {
            this(0.5, 0.5);

        }

    }
}
