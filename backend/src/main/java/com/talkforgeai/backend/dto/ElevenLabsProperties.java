package com.talkforgeai.backend.dto;

public record ElevenLabsProperties(String voiceId, String modelId) {

    public ElevenLabsProperties() {
        this("21m00Tcm4TlvDq8ikWAM", "eleven_multilingual_v1");
    }
}
