package com.talkforgeai.backend.chat.service;

import com.talkforgeai.backend.chat.domain.ChatMessageEntity;
import com.talkforgeai.backend.chat.domain.ChatMessageType;
import com.talkforgeai.backend.chat.repository.ChatMessageRepository;
import com.talkforgeai.backend.persona.domain.GlobalSystem;
import com.talkforgeai.backend.persona.domain.PersonaEntity;
import com.talkforgeai.backend.session.domain.ChatSessionEntity;
import com.talkforgeai.service.openai.dto.OpenAIChatMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    private final ChatMessageRepository repository;
    private final SystemService systemService;

    public MessageService(ChatMessageRepository repository, SystemService systemService) {
        this.repository = repository;
        this.systemService = systemService;
    }

    public List<ChatMessageEntity> getMessages(UUID sessionId, ChatMessageType type) {
        return repository.findAllByChatSessionIdAndType(sessionId, type);
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

    public OpenAIChatMessage getLastProcessedMessage(UUID sessionId) {
        return mapToDto(repository.findLastProcessedMessage(sessionId));
    }

    public List<OpenAIChatMessage> getPreviousMessages(ChatSessionEntity session) {
        List<OpenAIChatMessage> previousMessages;
        previousMessages = session.getChatMessages().stream()
                .filter(m -> m.getType() == ChatMessageType.UNPROCESSED)
                .map(this::mapToDto)
                .toList();
        return previousMessages;
    }

    public List<OpenAIChatMessage> composeMessagePayload(List<OpenAIChatMessage> previousMessages, OpenAIChatMessage newMessage, PersonaEntity persona) {
        List<OpenAIChatMessage> messages = new ArrayList<>();

        List<GlobalSystem> globalSystems = persona.getGlobalSystems();
        globalSystems.forEach(s -> {
            messages.add(new OpenAIChatMessage(OpenAIChatMessage.Role.SYSTEM, systemService.getContent(s)));
        });

        messages.add(new OpenAIChatMessage(OpenAIChatMessage.Role.SYSTEM, persona.getSystem()));
        messages.addAll(previousMessages);
        messages.add(newMessage);
        return messages;
    }
}
