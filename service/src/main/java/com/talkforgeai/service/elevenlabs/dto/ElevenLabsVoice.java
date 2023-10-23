package com.talkforgeai.service.elevenlabs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record ElevenLabsVoice(@JsonProperty("voice_id") String voiceId, String name, String category,
                              Labels labels) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Labels(String accent, String description, String age, String gender,
                         @JsonProperty("use case") String useCase) {
    }

}
