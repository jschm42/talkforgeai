package com.talkforgeai.backend.dto;

import java.util.UUID;

public record PersonaResponse(UUID personaId,
                              String name,
                              String description,
                              String imageUrl) {
}
