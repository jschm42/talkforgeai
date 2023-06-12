package com.talkforgeai.talkforgeaiserver.service;

import com.talkforgeai.talkforgeaiserver.domain.ChatMessageEntity;
import com.talkforgeai.talkforgeaiserver.domain.ChatMessageType;
import com.talkforgeai.talkforgeaiserver.domain.ChatSessionEntity;
import com.talkforgeai.talkforgeaiserver.domain.PersonaEntity;
import com.talkforgeai.talkforgeaiserver.exception.SessionException;
import com.talkforgeai.talkforgeaiserver.repository.ChatSessionRepository;
import com.talkforgeai.talkforgeaiserver.util.StringUtils;
import com.theokanning.openai.completion.chat.ChatMessage;
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

    public ChatSessionEntity update(UUID sessionId, List<ChatMessage> messages, List<ChatMessage> processedMessages) {
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

    public ChatSessionEntity create(PersonaEntity persona, List<ChatMessage> messages, List<ChatMessage> processedMessages) {
        ChatSessionEntity session = new ChatSessionEntity();
        session.setTitle("<empty>");
        session.setDescription("<empty>");
        session.setPersona(persona);

        return save(messages, processedMessages, session);
    }

    private ChatSessionEntity save(List<ChatMessage> messages, List<ChatMessage> processedMessages, ChatSessionEntity session) {
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
