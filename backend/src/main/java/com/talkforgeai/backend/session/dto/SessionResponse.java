package com.talkforgeai.backend.session.dto;

import com.talkforgeai.backend.persona.dto.PersonaResponse;
import com.talkforgeai.service.openai.dto.OpenAIChatMessage;

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