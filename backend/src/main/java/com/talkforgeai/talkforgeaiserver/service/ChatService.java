package com.talkforgeai.talkforgeaiserver.service;

import com.talkforgeai.talkforgeaiserver.domain.*;
import com.talkforgeai.talkforgeaiserver.dto.ChatCompletionRequest;
import com.talkforgeai.talkforgeaiserver.dto.ChatCompletionResponse;
import com.talkforgeai.talkforgeaiserver.dto.NewChatSessionRequest;
import com.talkforgeai.talkforgeaiserver.dto.SessionResponse;
import com.talkforgeai.talkforgeaiserver.dto.ws.ChatResponseMessage;
import com.talkforgeai.talkforgeaiserver.dto.ws.ChatStatusMessage;
import com.talkforgeai.talkforgeaiserver.exception.PersonaException;
import com.talkforgeai.talkforgeaiserver.exception.SessionException;
import com.talkforgeai.talkforgeaiserver.transformers.MessageProcessor;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class ChatService {
    private final OpenAIChatService openAIChatService;
    private final PersonaService personaService;
    private final SessionService sessionService;
    private final MessageService messageService;
    private final WebSocketService webSocketService;
    private final MessageProcessor messageProcessor;
    private final FileStorageService fileStorageService;
    Logger logger = LoggerFactory.getLogger(ChatService.class);

    public ChatService(OpenAIChatService openAIChatService,
                       SessionService sessionService,
                       PersonaService personaService,
                       MessageService messageService,
                       WebSocketService webSocketService,
                       MessageProcessor messageProcessor,
                       FileStorageService fileStorageService) {
        this.openAIChatService = openAIChatService;
        this.sessionService = sessionService;
        this.personaService = personaService;
        this.messageService = messageService;
        this.webSocketService = webSocketService;
        this.messageProcessor = messageProcessor;
        this.fileStorageService = fileStorageService;
    }

    public UUID create(NewChatSessionRequest request) {
        logger.info("Creating new chat session for persona: {}", request.personaId());

        PersonaEntity persona = personaService.getPersonaById(request.personaId())
                .orElseThrow(() -> new PersonaException("Persona not found: " + request.personaId()));

        ChatSessionEntity session
                = sessionService.create(persona, new ArrayList<>(), new ArrayList<>());

        return session.getId();
    }

    private Map<String, String> mapToGptProperties(Map<String, PropertyEntity> personaProperties) {
        Map<String, String> gptProperties = new HashMap<>();
        personaProperties.forEach((key, value) -> {
            if (value.getCategory() == PropertyCategory.CHATGPT) {
                gptProperties.put(key, value.getPropertyValue());
            }
        });
        return gptProperties;
    }

    @Async
    @Transactional
    public CompletableFuture<ChatCompletionResponse> submit(ChatCompletionRequest request) {
        logger.info("Submitting chat completion request for session: {}", request.sessionId());

        ChatSessionEntity session = sessionService.getById(request.sessionId())
                .orElseThrow(() -> new SessionException("Session not found: " + request.sessionId()));

        PersonaEntity persona = session.getPersona();
        List<ChatMessage> previousMessages = getPreviousMessages(session);
        boolean isFirstSubmitInSession = previousMessages.isEmpty();

        ChatMessage newUserMessage = new ChatMessage(ChatMessageRole.USER.value(), request.prompt());
        // TODO Postprocessing of new user message
        ChatMessage processedNewUserMessage = new ChatMessage(ChatMessageRole.USER.value(), request.prompt());

        List<ChatMessage> messagePayload = composeMessagePayload(previousMessages, processedNewUserMessage, persona);

        webSocketService.sendMessage(
                new ChatStatusMessage(request.sessionId(), "Thinking...")
        );

        List<ChatCompletionChoice> choices
                = openAIChatService.submit(messagePayload, mapToGptProperties(persona.getProperties()));

        List<ChatMessage> responseMessages = choices.stream()
                .map(ChatCompletionChoice::getMessage)
                .toList();

        List<ChatMessage> messagesToSave = new ArrayList<>();
        messagesToSave.add(newUserMessage);
        messagesToSave.addAll(responseMessages);

        List<ChatMessage> processedMessagesToSave = new ArrayList<>();

        webSocketService.sendMessage(
                new ChatStatusMessage(request.sessionId(), "Processing...")
        );
        List<ChatMessage> processedResponseMessages = responseMessages.stream()
                .map(m -> messageProcessor.transform(m, session.getId(), fileStorageService.getDataDirectory()))
                .toList();

        processedMessagesToSave.add(processedNewUserMessage);
        processedMessagesToSave.addAll(processedResponseMessages);

        if (isFirstSubmitInSession) {
            sessionService.update(request.sessionId(), newUserMessage.getContent(), "<empty>");
        }
        ChatSessionEntity updatedSession
                = sessionService.update(request.sessionId(), messagesToSave, processedMessagesToSave);


        webSocketService.sendMessage(
                new ChatStatusMessage(request.sessionId(), "")
        );

        webSocketService.sendMessage(
                new ChatResponseMessage(request.sessionId(), processedResponseMessages)
        );

        return CompletableFuture.completedFuture(createResponse(processedResponseMessages, updatedSession));
    }

    private List<ChatMessage> getPreviousMessages(ChatSessionEntity session) {
        List<ChatMessage> previousMessages;
        previousMessages = session.getChatMessages().stream()
                .filter(m -> m.getType() == ChatMessageType.UNPROCESSED)
                .map(messageService::mapToDto)
                .toList();
        return previousMessages;
    }


    public List<SessionResponse> getSessions() {
        List<ChatSessionEntity> allSessions = sessionService.getAll();
        return allSessions.stream()
                .map(this::mapSessionEntity)
                .toList();
    }

    private ChatCompletionResponse createResponse(List<ChatMessage> processedMessages, ChatSessionEntity updatedSession) {
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

    private SessionResponse mapSessionEntity(ChatSessionEntity session) {
        return new SessionResponse(
                session.getId(),
                session.getTitle(),
                session.getDescription());
    }

}
