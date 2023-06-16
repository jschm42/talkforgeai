package com.talkforgeai.talkforgeaiserver.service;

import com.talkforgeai.talkforgeaiserver.domain.*;
import com.talkforgeai.talkforgeaiserver.dto.*;
import com.talkforgeai.talkforgeaiserver.dto.ws.WSChatResponseMessage;
import com.talkforgeai.talkforgeaiserver.dto.ws.WSChatStatusMessage;
import com.talkforgeai.talkforgeaiserver.exception.PersonaException;
import com.talkforgeai.talkforgeaiserver.exception.SessionException;
import com.talkforgeai.talkforgeaiserver.openai.OpenAIChatService;
import com.talkforgeai.talkforgeaiserver.openai.dto.OpenAIChatMessage;
import com.talkforgeai.talkforgeaiserver.openai.dto.OpenAIRequest;
import com.talkforgeai.talkforgeaiserver.openai.dto.OpenAIResponse;
import com.talkforgeai.talkforgeaiserver.transformers.MessageProcessor;
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

    private final FunctionRepository functionRepository;
    Logger logger = LoggerFactory.getLogger(ChatService.class);

    public ChatService(OpenAIChatService openAIChatService,
                       SessionService sessionService,
                       PersonaService personaService,
                       MessageService messageService,
                       WebSocketService webSocketService,
                       MessageProcessor messageProcessor,
                       FileStorageService fileStorageService,
                       FunctionRepository functionRepository) {
        this.openAIChatService = openAIChatService;
        this.sessionService = sessionService;
        this.personaService = personaService;
        this.messageService = messageService;
        this.webSocketService = webSocketService;
        this.messageProcessor = messageProcessor;
        this.fileStorageService = fileStorageService;
        this.functionRepository = functionRepository;
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

    private OpenAIResponse submit(List<OpenAIChatMessage> messages, Map<String, String> properties) {
        try {
            OpenAIRequest request = new OpenAIRequest();
            request.setMessages(messages);

            // TODO Properties setzen
            if (properties.containsKey(PropertyKeys.CHATGPT_MAX_TOKENS)) {
                request.setMaxTokens(Integer.valueOf(properties.get(PropertyKeys.CHATGPT_MAX_TOKENS)));
            }

            if (properties.containsKey(PropertyKeys.CHATGPT_TOP_P)) {
                request.setTopP(Double.valueOf(properties.get(PropertyKeys.CHATGPT_TOP_P)));
            }

            if (properties.containsKey(PropertyKeys.CHATGPT_MODEL)) {
                request.setModel(properties.get(PropertyKeys.CHATGPT_MODEL));
            }

            if (properties.containsKey(PropertyKeys.CHATGPT_FREQUENCY_PENALTY)) {
                request.setFrequencyPenalty(Double.valueOf(properties.get(PropertyKeys.CHATGPT_FREQUENCY_PENALTY)));
            }

            if (properties.containsKey(PropertyKeys.CHATGPT_FREQUENCY_PENALTY)) {
                request.setPresencePenalty(Double.valueOf(properties.get(PropertyKeys.CHATGPT_PRESENCE_PENALTY)));
            }

            if (properties.containsKey(PropertyKeys.CHATGPT_TEMPERATURE)) {
                request.setTemperature(Double.valueOf(properties.get(PropertyKeys.CHATGPT_TEMPERATURE)));
            }

            request.setFunctions(functionRepository.getAll());


            return openAIChatService.submit(request);
        } catch (Exception e) {
            logger.error("Error while submitting chat request.", e);
        }

        return null;
    }

    @Async
    @Transactional
    public CompletableFuture<ChatCompletionResponse> submit(ChatCompletionRequest request) {
        logger.info("Submitting chat completion request for session: {}", request.sessionId());

        try {
            ChatSessionEntity session = sessionService.getById(request.sessionId())
                    .orElseThrow(() -> new SessionException("Session not found: " + request.sessionId()));

            PersonaEntity persona = session.getPersona();
            List<OpenAIChatMessage> previousMessages = getPreviousMessages(session);
            boolean isFirstSubmitInSession = previousMessages.isEmpty();

            OpenAIChatMessage newUserMessage = new OpenAIChatMessage(OpenAIChatMessage.Role.USER, request.prompt());
            // TODO Postprocessing of new user message
            OpenAIChatMessage processedNewUserMessage = new OpenAIChatMessage(OpenAIChatMessage.Role.USER, request.prompt());

            List<OpenAIChatMessage> messagePayload = composeMessagePayload(previousMessages, processedNewUserMessage, persona);

            webSocketService.sendMessage(
                    new WSChatStatusMessage(request.sessionId(), "Thinking...")
            );

            OpenAIResponse response = submit(messagePayload, mapToGptProperties(persona.getProperties()));

            List<OpenAIChatMessage> responseMessages = response.choices().stream()
                    .map(OpenAIResponse.ResponseChoice::message)
                    .toList();

            List<OpenAIChatMessage> messagesToSave = new ArrayList<>();
            messagesToSave.add(newUserMessage);
            messagesToSave.addAll(responseMessages);

            List<OpenAIChatMessage> processedMessagesToSave = new ArrayList<>();

            webSocketService.sendMessage(
                    new WSChatStatusMessage(request.sessionId(), "Processing...")
            );
            List<OpenAIChatMessage> processedResponseMessages = responseMessages.stream()
                    .map(m -> {
                        if (m.content() == null) {
                            return m;
                        }
                        return messageProcessor.transform(m, session.getId(), fileStorageService.getDataDirectory());
                    })
                    .toList();

            processedMessagesToSave.add(processedNewUserMessage);
            processedMessagesToSave.addAll(processedResponseMessages);

            if (isFirstSubmitInSession) {
                sessionService.update(request.sessionId(), newUserMessage.content(), "<empty>");
            }
            ChatSessionEntity updatedSession
                    = sessionService.update(request.sessionId(), messagesToSave, processedMessagesToSave);


            webSocketService.sendMessage(
                    new WSChatStatusMessage(request.sessionId(), "")
            );

            webSocketService.sendMessage(
                    new WSChatResponseMessage(request.sessionId(), processedResponseMessages)
            );

            return CompletableFuture.completedFuture(createResponse(processedResponseMessages, updatedSession));
        } catch (Exception e) {
            logger.error("Error while processing chat request.", e);
        }
        return null;
    }

    private List<OpenAIChatMessage> getPreviousMessages(ChatSessionEntity session) {
        List<OpenAIChatMessage> previousMessages;
        previousMessages = session.getChatMessages().stream()
                .filter(m -> m.getType() == ChatMessageType.UNPROCESSED)
                .map(messageService::mapToDto)
                .toList();
        return previousMessages;
    }


    public List<SessionResponse> getSessions() {
        List<ChatSessionEntity> allSessions = sessionService.getAllMostRecentFirst();
        return allSessions.stream()
                .map(this::mapSessionEntity)
                .toList();
    }

    private ChatCompletionResponse createResponse(List<OpenAIChatMessage> processedMessages, ChatSessionEntity updatedSession) {
        return new ChatCompletionResponse(updatedSession.getId().toString(), processedMessages);
    }

    private List<OpenAIChatMessage> composeMessagePayload(List<OpenAIChatMessage> previousMessages, OpenAIChatMessage newMessage, PersonaEntity persona) {
        List<OpenAIChatMessage> messages = new ArrayList<>();
        messages.add(new OpenAIChatMessage(OpenAIChatMessage.Role.SYSTEM, SystemService.IMAGE_GEN_SYSTEM));
        messages.add(new OpenAIChatMessage(OpenAIChatMessage.Role.SYSTEM, persona.getSystem()));
        messages.addAll(previousMessages);
        messages.add(newMessage);
        return messages;
    }

    private SessionResponse mapSessionEntity(ChatSessionEntity session) {
        List<ChatMessageEntity> processedMessages
                = session.getChatMessages().stream()
                .filter(m -> m.getType() == ChatMessageType.PROCESSED)
                .toList();

        return new SessionResponse(
                session.getId(),
                session.getTitle(),
                session.getDescription(),
                session.getCreatedAt(),
                messageService.mapToDto(processedMessages),
                personaService.mapPersonaResponse(session.getPersona()));
    }

    public SessionResponse getSession(UUID sessionId) {
        Optional<ChatSessionEntity> session = sessionService.getById(sessionId);
        if (session.isPresent()) {
            return mapSessionEntity(session.get());
        }
        throw new SessionException("Session not found: " + sessionId);
    }
}
