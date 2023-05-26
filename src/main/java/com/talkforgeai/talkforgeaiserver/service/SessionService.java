package com.talkforgeai.talkforgeaiserver.service;

import com.talkforgeai.talkforgeaiserver.domain.ChatMessageEntity;
import com.talkforgeai.talkforgeaiserver.domain.ChatMessageType;
import com.talkforgeai.talkforgeaiserver.domain.ChatSessionEntity;
import com.talkforgeai.talkforgeaiserver.domain.PersonaEntity;
import com.talkforgeai.talkforgeaiserver.repository.ChatSessionRepository;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {
    private final ChatSessionRepository repository;
    private final MessageService messageService;

    public SessionService(ChatSessionRepository repository, MessageService messageService) {
        this.repository = repository;
        this.messageService = messageService;
    }

    public Optional<ChatSessionEntity> getSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty() || sessionId.isBlank()) {
            return Optional.empty();
        }

        return this.repository.findById(UUID.fromString(sessionId));
    }

    public ChatSessionEntity updateChatSession(String sessionId, List<ChatMessage> messages, List<ChatMessage> processedMessages) {
        ChatSessionEntity session = repository.getReferenceById(UUID.fromString(sessionId));

        return saveChatSession(messages, processedMessages, session);
    }

    public ChatSessionEntity createChatSession(PersonaEntity persona, List<ChatMessage> messages, List<ChatMessage> processedMessages) {
        ChatSessionEntity session = new ChatSessionEntity();
        session.setPersona(persona);

        return saveChatSession(messages, processedMessages, session);
    }

    private ChatSessionEntity saveChatSession(List<ChatMessage> messages, List<ChatMessage> processedMessages, ChatSessionEntity session) {
        List<ChatMessageEntity> messageEntities = messageService.mapToEntity(messages, session, ChatMessageType.UNPROCESSED);
        List<ChatMessageEntity> processedMessageEntities = messageService.mapToEntity(processedMessages, session, ChatMessageType.PROCESSED);

        session.getChatMessages().addAll(messageEntities);
        session.getChatMessages().addAll(processedMessageEntities);
        return repository.save(session);
    }

}
