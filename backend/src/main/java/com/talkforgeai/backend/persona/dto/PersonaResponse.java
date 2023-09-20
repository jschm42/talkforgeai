package com.talkforgeai.backend.persona.dto;

import java.util.Map;
import java.util.UUID;

public record PersonaResponse(UUID personaId,
                              String name,
                              String description,
                              String imageUrl,
                              Map<String, String> properties) {
}
