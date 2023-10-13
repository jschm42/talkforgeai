package com.talkforgeai.backend.persona.dto;

import com.talkforgeai.backend.persona.domain.GlobalSystem;
import com.talkforgeai.backend.persona.domain.RequestFunction;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record PersonaDto(UUID personaId,
                         String name,
                         String description,
                         String system,

                         List<GlobalSystem> globalSystems,
                         List<RequestFunction> requestFunctions,
                         String imageUrl,
                         String imagePath,
                         Map<String, String> properties) {
    @Override
    public String toString() {
        return "PersonaDto{" +
                "personaId=" + personaId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", system='" + system + '\'' +
                ", globalSystems=" + globalSystems +
                ", requestFunctions=" + requestFunctions +
                ", imageUrl='" + imageUrl + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", properties=" + properties +
                '}';
    }
}
