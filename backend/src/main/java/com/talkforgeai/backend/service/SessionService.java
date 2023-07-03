package com.talkforgeai.backend.service;

import com.talkforgeai.backend.domain.ChatMessageEntity;
import com.talkforgeai.backend.domain.ChatMessageType;
import com.talkforgeai.backend.domain.ChatSessionEntity;
import com.talkforgeai.backend.domain.PersonaEntity;
import com.talkforgeai.backend.exception.SessionException;
import com.talkforgeai.backend.openai.dto.OpenAIChatMessage;
import com.talkforgeai.backend.repository.ChatSessionRepository;
import com.talkforgeai.backend.util.StringUtils;
import org.springframework.data.domain.Sort;
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

    public Optional<ChatSessionEntity> getById(UUID sessionId) {
        if (sessionId == null) {
            return Optional.empty();
        }

        return this.repository.findById(sessionId);
    }

    public ChatSessionEntity update(UUID sessionId, List<OpenAIChatMessage> messages, List<OpenAIChatMessage> processedMessages) {
        ChatSessionEntity session = repository.findById(sessionId)
                .orElseThrow(() -> new SessionException("Session not found: " + sessionId));

        return save(messages, processedMessages, session);
    }

    public void update(UUID sessionId, String title, String description) {
        ChatSessionEntity session = repository.findById(sessionId)
                .orElseThrow(() -> new SessionException("Session not found: " + sessionId));

        session.setTitle(StringUtils.maxLengthString(title, 29) + "...");
        session.setDescription(StringUtils.maxLengthString(description, 256));
        repository.save(session);
    }

    public ChatSessionEntity create(PersonaEntity persona, List<OpenAIChatMessage> messages, List<OpenAIChatMessage> processedMessages) {
        ChatSessionEntity session = new ChatSessionEntity();
        session.setTitle("<empty>");
        session.setDescription("<empty>");
        session.setPersona(persona);

        return save(messages, processedMessages, session);
    }

    private ChatSessionEntity save(List<OpenAIChatMessage> messages, List<OpenAIChatMessage> processedMessages, ChatSessionEntity session) {
        List<ChatMessageEntity> messageEntities = messageService.mapToEntity(messages, session, ChatMessageType.UNPROCESSED);
        List<ChatMessageEntity> processedMessageEntities = messageService.mapToEntity(processedMessages, session, ChatMessageType.PROCESSED);

        session.getChatMessages().addAll(messageEntities);
        session.getChatMessages().addAll(processedMessageEntities);
        return repository.save(session);
    }

    public List<ChatSessionEntity> getAllMostRecentFirst() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
