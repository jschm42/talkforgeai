package com.talkforgeai.backend.session.service;

import com.talkforgeai.backend.chat.domain.ChatMessageEntity;
import com.talkforgeai.backend.chat.domain.ChatMessageType;
import com.talkforgeai.backend.chat.repository.ChatMessageRepository;
import com.talkforgeai.backend.chat.service.SystemService;
import com.talkforgeai.backend.persona.domain.GlobalSystem;
import com.talkforgeai.backend.persona.domain.PersonaEntity;
import com.talkforgeai.backend.session.domain.ChatSessionEntity;
import com.talkforgeai.backend.session.dto.SessionResponse;
import com.talkforgeai.backend.session.dto.UpdateSessionTitleRequest;
import com.talkforgeai.backend.session.exception.SessionException;
import com.talkforgeai.backend.session.repository.ChatSessionRepository;
import com.talkforgeai.backend.util.StringUtils;
import com.talkforgeai.service.openai.dto.OpenAIChatMessage;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {
    private final ChatMessageRepository messageRepository;
    private final ChatSessionRepository sessionRepository;
    private final SessionMapper sessionMapper;

    private final SystemService systemService;

    public SessionService(ChatSessionRepository sessionRepository, SessionMapper sessionMapper, ChatMessageRepository messageRepository, SystemService systemService) {
        this.sessionRepository = sessionRepository;
        this.sessionMapper = sessionMapper;
        this.messageRepository = messageRepository;
        this.systemService = systemService;
    }

    public Optional<ChatSessionEntity> getById(UUID sessionId) {
        if (sessionId == null) {
            return Optional.empty();
        }

        return this.sessionRepository.findById(sessionId);
    }


    public SessionResponse getSession(UUID sessionId) {
        Optional<ChatSessionEntity> session = getById(sessionId);
        if (session.isPresent()) {
            return sessionMapper.mapSessionEntity(session.get());
        }
        throw new SessionException("Session not found: " + sessionId);
    }

    public List<SessionResponse> getSessions(UUID personaId) {
        List<ChatSessionEntity> allSessions = sessionRepository.getAllByPersonaId(
                personaId,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return allSessions.stream()
                .map(sessionMapper::mapSessionEntity)
                .toList();
    }

    @Transactional
    public ChatSessionEntity update(UUID sessionId, List<OpenAIChatMessage> messages, List<OpenAIChatMessage> processedMessages) {
        ChatSessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionException("Session not found: " + sessionId));

        return save(messages, processedMessages, session);
    }

    @Transactional
    public void update(UUID sessionId, String title, String description) {
        ChatSessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionException("Session not found: " + sessionId));

        session.setTitle(StringUtils.maxLengthString(title, 29) + "...");
        session.setDescription(StringUtils.maxLengthString(description, 256));
        sessionRepository.save(session);
    }

    @Transactional
    public ChatSessionEntity create(PersonaEntity persona, List<OpenAIChatMessage> messages, List<OpenAIChatMessage> processedMessages) {
        ChatSessionEntity session = new ChatSessionEntity();
        session.setTitle("<empty>");
        session.setDescription("<empty>");
        session.setPersona(persona);

        return save(messages, processedMessages, session);
    }

    private ChatSessionEntity save(List<OpenAIChatMessage> messages, List<OpenAIChatMessage> processedMessages, ChatSessionEntity session) {
        List<ChatMessageEntity> messageEntities = sessionMapper.mapToEntity(messages, session, ChatMessageType.UNPROCESSED);
        List<ChatMessageEntity> processedMessageEntities = sessionMapper.mapToEntity(processedMessages, session, ChatMessageType.PROCESSED);

        session.getChatMessages().addAll(messageEntities);
        session.getChatMessages().addAll(processedMessageEntities);
        return sessionRepository.save(session);
    }

    public List<ChatSessionEntity> getAllMostRecentFirst() {
        return sessionRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }


    public List<ChatMessageEntity> getMessages(UUID sessionId, ChatMessageType type) {
        return messageRepository.findAllByChatSessionIdAndType(sessionId, type);
    }

    public OpenAIChatMessage getLastProcessedMessage(UUID sessionId) {
        return sessionMapper.mapToDto(messageRepository.findLastProcessedMessage(sessionId));
    }

    public List<OpenAIChatMessage> getPreviousMessages(ChatSessionEntity session) {
        List<OpenAIChatMessage> previousMessages;
        previousMessages = session.getChatMessages().stream()
                .filter(m -> m.getType() == ChatMessageType.UNPROCESSED)
                .map(sessionMapper::mapToDto)
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


    @Transactional
    public ChatMessageEntity saveMessage(UUID sessionId, OpenAIChatMessage message, ChatMessageType type) {
        ChatSessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionException("Session not found: " + sessionId));

        ChatMessageEntity chatMessageEntity = sessionMapper.mapToEntity(message, session, type);
        session.getChatMessages().add(chatMessageEntity);

        sessionRepository.save(session);
        return chatMessageEntity;
    }

    public OpenAIChatMessage mapToOpenAIMessage(ChatMessageEntity chatMessageEntity) {
        return sessionMapper.mapToDto(chatMessageEntity);
    }

    public void updateSessionTitle(UUID sessionId, UpdateSessionTitleRequest request) {
        ChatSessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionException("Session not found: " + sessionId));

        session.setTitle(request.newTitle());
        sessionRepository.save(session);
    }

    @Transactional
    public void deleteSession(UUID sessionId) {
        sessionRepository.deleteById(sessionId);
    }
}
