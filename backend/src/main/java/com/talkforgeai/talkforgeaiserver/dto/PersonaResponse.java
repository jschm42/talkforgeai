package com.talkforgeai.talkforgeaiserver.dto;

import java.util.UUID;

public record PersonaResponse(UUID personaId,
                              String name,
                              String description,
                              String imageUrl) {
}
