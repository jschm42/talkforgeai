package com.talkforgeai.talkforgeaiserver.dto;

import java.util.UUID;

public record PersonaResponse(UUID id,
                              String name,
                              String description,
                              String imageUrl) {
}
