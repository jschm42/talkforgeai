package com.talkforgeai.talkforgeaiserver.service;

import com.talkforgeai.talkforgeaiserver.domain.ChatMessageEntity;
import com.talkforgeai.talkforgeaiserver.domain.ChatMessageType;
import com.talkforgeai.talkforgeaiserver.domain.ChatSessionEntity;
import com.talkforgeai.talkforgeaiserver.repository.ChatMessageRepository;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
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

    public List<ChatMessage> mapToDto(List<ChatMessageEntity> entities) {
        return entities.stream()
                .map(this::mapToDto)
                .toList();
    }

    public ChatMessage mapToDto(ChatMessageEntity entity) {
        ChatMessage dto = new ChatMessage();
        dto.setRole(entity.getRole().value());
        dto.setContent(entity.getContent());
        return dto;
    }

    private ChatMessageEntity mapToEntity(ChatMessage message, ChatSessionEntity session, ChatMessageType type) {
        ChatMessageEntity entity = new ChatMessageEntity();
        entity.setType(type);
        entity.setChatSession(session);
        entity.setContent(message.getContent());
        entity.setRole(ChatMessageRole.valueOf(message.getRole().toUpperCase()));
        return entity;
    }


    public List<ChatMessageEntity> mapToEntity(List<ChatMessage> messages, ChatSessionEntity session, ChatMessageType type) {
        return messages.stream()
                .map(m -> mapToEntity(m, session, type))
                .toList();
    }

    public ChatMessage getLastProcessedMessage(UUID sessionId) {
        return mapToDto(repository.findLastProcessedMessage(sessionId));
    }
}
