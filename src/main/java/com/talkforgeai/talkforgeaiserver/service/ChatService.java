package com.talkforgeai.talkforgeaiserver.service;

import com.talkforgeai.talkforgeaiserver.domain.ChatMessageEntity;
import com.talkforgeai.talkforgeaiserver.domain.ChatMessageType;
import com.talkforgeai.talkforgeaiserver.domain.ChatSessionEntity;
import com.talkforgeai.talkforgeaiserver.domain.PersonaEntity;
import com.talkforgeai.talkforgeaiserver.repository.ChatSessionRepository;
import com.talkforgeai.talkforgeaiserver.service.dto.ChatCompletionRequest;
import com.talkforgeai.talkforgeaiserver.service.dto.ChatCompletionResponse;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private final OpenAIChatService openAIChatService;
    private final PersonaService personaService;

    private final ChatSessionRepository sessionRepository;

    public ChatService(OpenAIChatService openAIChatService,
                       ChatSessionRepository sessionRepository,
                       PersonaService personaService) {
        this.openAIChatService = openAIChatService;
        this.sessionRepository = sessionRepository;
        this.personaService = personaService;
    }

    public ChatCompletionResponse submit(ChatCompletionRequest request) {
        PersonaEntity persona = personaService.getPersona(request.personaId());

        ChatMessage newUserMessage = new ChatMessage(ChatMessageRole.USER.value(), request.prompt());

        List<ChatMessage> messages = composeMessagePayload(null, newUserMessage, persona);

        List<ChatCompletionChoice> choices = openAIChatService.submit(messages);

        List<ChatMessage> processedMessages = choices.stream()
            .map(ChatCompletionChoice::getMessage)
            .toList();

        messages.addAll(processedMessages);

        ChatSessionEntity updatedSession;
        if (existsChatSession(request.sessionId())) {
            updatedSession = updateChatSession(request.sessionId(), messages);
        } else {
            updatedSession = createChatSession(persona, messages);
        }

        return new ChatCompletionResponse(updatedSession.getId().toString(), processedMessages);
    }

    private List<ChatMessage> composeMessagePayload(List<ChatMessage> previousMessages, ChatMessage newMessage, PersonaEntity persona) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), SystemService.IMAGE_GEN_SYSTEM));
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), persona.getSystem()));
        messages.addAll(previousMessages);
        messages.add(newMessage);
        return messages;
    }

    private boolean existsChatSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return false;
        }

        return sessionRepository.findById(UUID.fromString(sessionId)).isPresent();
    }

    private ChatSessionEntity updateChatSession(String sessionId, List<ChatMessage> messages) {
        ChatSessionEntity session = sessionRepository.getReferenceById(UUID.fromString(sessionId));
        List<ChatMessageEntity> messageEntities = mapToEntity(messages, session, ChatMessageType.DEFAULT);
        session.getChatMessages().addAll(messageEntities);
        return sessionRepository.save(session);
    }

    private ChatSessionEntity createChatSession(PersonaEntity persona, List<ChatMessage> messages) {
        ChatSessionEntity session = new ChatSessionEntity();
        session.setPersona(persona);
        List<ChatMessageEntity> messageEntities = mapToEntity(messages, session, ChatMessageType.DEFAULT);
        session.getChatMessages().addAll(messageEntities);
        return sessionRepository.save(session);
    }

    private List<ChatMessageEntity> mapToEntity(List<ChatMessage> messages, ChatSessionEntity session, ChatMessageType type) {
        return messages.stream()
            .map(m -> mapToEntity(m, session, type))
            .toList();
    }

    private ChatMessageEntity mapToEntity(ChatMessage message, ChatSessionEntity session, ChatMessageType type) {
        ChatMessageEntity entity = new ChatMessageEntity();
        entity.setType(type);
        entity.setChatSession(session);
        entity.setMessage(message.getContent());
        entity.setRole(message.getRole());
        return entity;
    }
}
