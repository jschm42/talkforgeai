package com.talkforgeai.talkforgeaiserver.service;

import com.talkforgeai.talkforgeaiserver.domain.ChatMessageEntity;
import com.talkforgeai.talkforgeaiserver.domain.ChatMessageType;
import com.talkforgeai.talkforgeaiserver.domain.ChatSessionEntity;
import com.talkforgeai.talkforgeaiserver.openai.OpenAIChatMessage;
import com.talkforgeai.talkforgeaiserver.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    private final ChatMessageRepository repository;

    public MessageService(ChatMessageRepository repository) {
        this.repository = repository;
    }

    public List<ChatMessageEntity> getMessages(String sessionId, ChatMessageType type) {
        return repository.findAllByChatSessionIdAndType(UUID.fromString(sessionId), type);
    }

    public List<OpenAIChatMessage> mapToDto(List<ChatMessageEntity> entities) {
        return entities.stream()
                .map(this::mapToDto)
                .toList();
    }

    public OpenAIChatMessage mapToDto(ChatMessageEntity entity) {
        OpenAIChatMessage dto = new OpenAIChatMessage();
        dto.setRole(entity.getRole());
        dto.setContent(entity.getContent());
        return dto;
    }

    private ChatMessageEntity mapToEntity(OpenAIChatMessage message, ChatSessionEntity session, ChatMessageType type) {
        ChatMessageEntity entity = new ChatMessageEntity();
        entity.setType(type);
        entity.setChatSession(session);
        entity.setContent(message.getContent());
        entity.setRole(message.getRole());
        return entity;
    }


    public List<ChatMessageEntity> mapToEntity(List<OpenAIChatMessage> messages, ChatSessionEntity session, ChatMessageType type) {
        return messages.stream()
                .map(m -> mapToEntity(m, session, type))
                .toList();
    }

    public OpenAIChatMessage getLastProcessedMessage(UUID sessionId) {
        return mapToDto(repository.findLastProcessedMessage(sessionId));
    }
}
