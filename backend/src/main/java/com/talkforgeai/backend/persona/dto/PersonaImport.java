package com.talkforgeai.backend.persona.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.talkforgeai.backend.persona.domain.GlobalSystem;
import com.talkforgeai.backend.persona.domain.RequestFunction;

import java.util.List;

public record PersonaImport(String version,
                            String name,
                            String description,
                            List<GlobalSystem> globalSystems,
                            List<RequestFunction> requestFunctions,
                            String system,
                            String imagePath,
                            @JsonProperty("chatgpt")
                            ChatGptConfig chatGptConfig,
                            @JsonProperty("elevenlabs")
                            ElevenLabsConfig elevenLabsConfig) {

    public record ChatGptConfig(String model,
                                String temperature,
                                @JsonProperty("top_p")
                                String topP) {

    }

    public record ElevenLabsConfig(String voiceId) {

    }
}
