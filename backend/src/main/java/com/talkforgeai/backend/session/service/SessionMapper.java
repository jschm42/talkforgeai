package com.talkforgeai.backend.session.service;

import com.talkforgeai.backend.chat.domain.ChatMessageEntity;
import com.talkforgeai.backend.chat.domain.ChatMessageType;
import com.talkforgeai.backend.persona.service.PersonaMapper;
import com.talkforgeai.backend.session.domain.ChatSessionEntity;
import com.talkforgeai.backend.session.dto.SessionResponse;
import com.talkforgeai.service.openai.dto.OpenAIChatMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SessionMapper {
    private final PersonaMapper personaMapper;

    public SessionMapper(PersonaMapper personaMapper) {
        this.personaMapper = personaMapper;
    }

    public SessionResponse mapSessionEntity(ChatSessionEntity session) {
        List<ChatMessageEntity> processedMessages
                = session.getChatMessages().stream()
                .filter(m -> m.getType() == ChatMessageType.PROCESSED)
                .toList();

        return new SessionResponse(
                session.getId(),
                session.getTitle(),
                session.getDescription(),
                session.getCreatedAt(),
                mapToDto(processedMessages),
                personaMapper.mapPersonaResponse(session.getPersona()));
    }


    public OpenAIChatMessage mapToDto(ChatMessageEntity entity) {
        OpenAIChatMessage.FunctionCall functionCall = null;
        if (entity.getFunctionCallName() != null) {
            functionCall = new OpenAIChatMessage.FunctionCall(entity.getFunctionCallName(), entity.getFunctionCallArguments());
        }
        return new OpenAIChatMessage(entity.getRole(), entity.getContent(), entity.getFunctionName(), functionCall);
    }

    public ChatMessageEntity mapToEntity(OpenAIChatMessage message, ChatSessionEntity session, ChatMessageType type) {
        ChatMessageEntity entity = new ChatMessageEntity();
        entity.setType(type);
        entity.setChatSession(session);
        entity.setContent(message.content());
        entity.setRole(message.role());
        entity.setFunctionName(message.name());
        if (message.functionCall() != null) {
            entity.setFunctionCallName(message.functionCall().name());
            entity.setFunctionCallArguments(message.functionCall().arguments());
        }
        return entity;
    }


    public List<ChatMessageEntity> mapToEntity(List<OpenAIChatMessage> messages, ChatSessionEntity session, ChatMessageType type) {
        return messages.stream()
                .map(m -> mapToEntity(m, session, type))
                .toList();
    }


    public List<OpenAIChatMessage> mapToDto(List<ChatMessageEntity> entities) {
        return entities.stream()
                .map(this::mapToDto)
                .toList();
    }

}
