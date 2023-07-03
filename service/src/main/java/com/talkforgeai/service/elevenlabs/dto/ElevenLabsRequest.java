package com.talkforgeai.service.elevenlabs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ElevenLabsRequest(String text,
                                String voiceId,
                                String modelId,
                                VoiceSettings voiceSettings) {

    public record VoiceSettings(double stability,
                                double similarityBoost) {

        public VoiceSettings() {
            this(0.5, 0.5);
        }

    }
}
