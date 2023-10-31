/*
 * Copyright (c) 2023 Jean Schmitz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.talkforgeai.backend.session.service;

import com.talkforgeai.backend.chat.domain.ChatMessageEntity;
import com.talkforgeai.backend.chat.domain.ChatMessageType;
import com.talkforgeai.backend.chat.repository.ChatMessageRepository;
import com.talkforgeai.backend.chat.service.SystemService;
import com.talkforgeai.backend.persona.domain.GlobalSystem;
import com.talkforgeai.backend.persona.domain.PersonaEntity;
import com.talkforgeai.backend.persona.domain.PersonaPropertyValue;
import com.talkforgeai.backend.session.domain.ChatSessionEntity;
import com.talkforgeai.backend.session.dto.GenerateSessionTitleRequest;
import com.talkforgeai.backend.session.dto.GenerateSessionTitleResponse;
import com.talkforgeai.backend.session.dto.SessionResponse;
import com.talkforgeai.backend.session.dto.UpdateSessionTitleRequest;
import com.talkforgeai.backend.session.exception.SessionException;
import com.talkforgeai.backend.session.repository.ChatSessionRepository;
import com.talkforgeai.backend.util.StringUtils;
import com.talkforgeai.service.openai.OpenAIChatService;
import com.talkforgeai.service.openai.dto.OpenAIChatMessage;
import com.talkforgeai.service.openai.dto.OpenAIChatRequest;
import com.talkforgeai.service.openai.dto.OpenAIChatResponse;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.talkforgeai.backend.persona.service.PersonaProperties.FEATURE_IMAGEGENERATION;
import static com.talkforgeai.backend.persona.service.PersonaProperties.FEATURE_PLANTUML;

@Service
public class SessionService {
    private final ChatMessageRepository messageRepository;
    private final ChatSessionRepository sessionRepository;
    private final SessionMapper sessionMapper;

    private final SystemService systemService;

    private final OpenAIChatService openAIChatService;

    public SessionService(ChatSessionRepository sessionRepository, SessionMapper sessionMapper, ChatMessageRepository messageRepository, SystemService systemService, OpenAIChatService openAIChatService) {
        this.sessionRepository = sessionRepository;
        this.sessionMapper = sessionMapper;
        this.messageRepository = messageRepository;
        this.systemService = systemService;
        this.openAIChatService = openAIChatService;
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

        if (!systemService.getContent(GlobalSystem.DEFAULT).isEmpty()) {
            messages.add(new OpenAIChatMessage(OpenAIChatMessage.Role.SYSTEM, systemService.getContent(GlobalSystem.DEFAULT)));
        }

        if (persona.getProperties().containsKey(FEATURE_IMAGEGENERATION.getKey())) {
            PersonaPropertyValue personaValue = persona.getProperties().get(FEATURE_IMAGEGENERATION.getKey());
            boolean isFeatureImageGeneration = "true".equalsIgnoreCase(personaValue.getPropertyValue());
            if (isFeatureImageGeneration) {
                messages.add(
                        new OpenAIChatMessage(OpenAIChatMessage.Role.SYSTEM, systemService.getContent(GlobalSystem.IMAGE_GEN))
                );
            }
        }

        if (persona.getProperties().containsKey(FEATURE_PLANTUML.getKey())) {
            PersonaPropertyValue personaValue = persona.getProperties().get(FEATURE_PLANTUML.getKey());
            boolean isFeaturePlantUML = "true".equalsIgnoreCase(personaValue.getPropertyValue());
            if (isFeaturePlantUML) {
                messages.add(
                        new OpenAIChatMessage(OpenAIChatMessage.Role.SYSTEM, systemService.getContent(GlobalSystem.PLANTUML))
                );
            }
        }

        String backgroundMessage = "";
        if (persona.getBackground() != null && !persona.getBackground().isEmpty()) {
            backgroundMessage = """
                    Use this background information to help you with your conversation: %s
                    """.formatted(persona.getBackground());
        }

        messages.add(new OpenAIChatMessage(OpenAIChatMessage.Role.SYSTEM, backgroundMessage));
        messages.add(new OpenAIChatMessage(
                OpenAIChatMessage.Role.SYSTEM,
                Objects.requireNonNullElse(persona.getPersonality(), ""))
        );
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

    @Transactional
    public GenerateSessionTitleResponse generateSessionTitle(UUID sessionId, GenerateSessionTitleRequest request) {
        ChatSessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionException("Session not found: " + sessionId));

        OpenAIChatRequest titleRequest = getTitleRequest(request);

        OpenAIChatResponse response = openAIChatService.submit(titleRequest);
        String generatedTitle = response.choices().get(0).message().content();

        session.setTitle(generatedTitle);
        sessionRepository.save(session);

        return new GenerateSessionTitleResponse(generatedTitle);
    }

    @NotNull
    private OpenAIChatRequest getTitleRequest(GenerateSessionTitleRequest request) {
        OpenAIChatRequest titleRequest = new OpenAIChatRequest();
        titleRequest.setModel("gpt-3.5-turbo");

        String content = """
                Generate a title in less than 6 words for the following message: %s
                """.formatted(request.userMessageContent());

        OpenAIChatMessage titleMessage = new OpenAIChatMessage(OpenAIChatMessage.Role.USER, content);
        titleRequest.setMessages(List.of(titleMessage));
        return titleRequest;
    }

}
