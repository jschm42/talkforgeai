package com.talkforgeai.backend.service;

import com.talkforgeai.backend.domain.ChatMessageEntity;
import com.talkforgeai.backend.domain.ChatMessageType;
import com.talkforgeai.backend.domain.ChatSessionEntity;
import com.talkforgeai.backend.openai.dto.OpenAIChatMessage;
import com.talkforgeai.backend.repository.ChatMessageRepository;
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
        OpenAIChatMessage.FunctionCall functionCall = null;
        if (entity.getFunctionCallName() != null) {
            functionCall = new OpenAIChatMessage.FunctionCall(entity.getFunctionCallName(), entity.getFunctionCallArguments());
        }
        return new OpenAIChatMessage(entity.getRole(), entity.getContent(), entity.getFunctionName(), functionCall);
    }

    private ChatMessageEntity mapToEntity(OpenAIChatMessage message, ChatSessionEntity session, ChatMessageType type) {
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

    public OpenAIChatMessage getLastProcessedMessage(UUID sessionId) {
        return mapToDto(repository.findLastProcessedMessage(sessionId));
    }
}
