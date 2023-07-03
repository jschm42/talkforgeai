package com.talkforgeai.backend.dto;

import com.talkforgeai.backend.openai.dto.OpenAIChatMessage;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record SessionResponse(UUID id,
                              String title,
                              String description,
                              Date createdAt,
                              List<OpenAIChatMessage> chatMessages,
                              PersonaResponse persona) {

}
