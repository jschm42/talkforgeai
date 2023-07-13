package com.talkforgeai.backend.chat.service;

import com.talkforgeai.backend.chat.PropertyKeys;
import com.talkforgeai.backend.chat.domain.ChatMessageType;
import com.talkforgeai.backend.chat.dto.ChatCompletionRequest;
import com.talkforgeai.backend.chat.repository.FunctionRepository;
import com.talkforgeai.backend.persona.domain.PersonaEntity;
import com.talkforgeai.backend.persona.domain.PropertyCategory;
import com.talkforgeai.backend.persona.domain.PropertyEntity;
import com.talkforgeai.backend.persona.domain.RequestFunction;
import com.talkforgeai.backend.persona.service.PersonaService;
import com.talkforgeai.backend.session.domain.ChatSessionEntity;
import com.talkforgeai.backend.session.exception.SessionException;
import com.talkforgeai.backend.session.service.SessionService;
import com.talkforgeai.backend.storage.FileStorageService;
import com.talkforgeai.backend.transformers.MessageProcessor;
import com.talkforgeai.backend.websocket.dto.WSChatStatusMessage;
import com.talkforgeai.backend.websocket.service.WebSocketService;
import com.talkforgeai.service.openai.OpenAIChatService;
import com.talkforgeai.service.openai.dto.OpenAIChatMessage;
import com.talkforgeai.service.openai.dto.OpenAIChatRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ChatStreamService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatStreamService.class);

    private final OpenAIChatService openAIChatService;
    private final PersonaService personaService;
    private final SessionService sessionService;
    private final MessageService messageService;
    private final WebSocketService webSocketService;
    private final MessageProcessor messageProcessor;
    private final FileStorageService fileStorageService;
    private final FunctionRepository functionRepository;

    private final SystemService systemService;


    public ChatStreamService(OpenAIChatService openAIChatService,
                             SessionService sessionService,
                             PersonaService personaService,
                             MessageService messageService,
                             WebSocketService webSocketService,
                             MessageProcessor messageProcessor,
                             FileStorageService fileStorageService,
                             FunctionRepository functionRepository,
                             SystemService systemService) {
        this.openAIChatService = openAIChatService;
        this.sessionService = sessionService;
        this.personaService = personaService;
        this.messageService = messageService;
        this.webSocketService = webSocketService;
        this.messageProcessor = messageProcessor;
        this.fileStorageService = fileStorageService;
        this.functionRepository = functionRepository;
        this.systemService = systemService;
    }

    public SseEmitter submit(ChatCompletionRequest request) {
        ChatSessionEntity session = sessionService.getById(request.sessionId())
                .orElseThrow(() -> new SessionException("Session not found: " + request.sessionId()));

        PersonaEntity persona = session.getPersona();
        List<OpenAIChatMessage> previousMessages = messageService.getPreviousMessages(session);
        boolean isFirstSubmitInSession = previousMessages.isEmpty();

        OpenAIChatMessage newUserMessage = new OpenAIChatMessage(OpenAIChatMessage.Role.USER, request.content());
        sessionService.saveMessage(session.getId(), newUserMessage, ChatMessageType.UNPROCESSED);

        // TODO Postprocessing of new user delta
        OpenAIChatMessage processedNewUserMessage = new OpenAIChatMessage(OpenAIChatMessage.Role.USER, request.content());
        sessionService.saveMessage(session.getId(), processedNewUserMessage, ChatMessageType.PROCESSED);

        List<OpenAIChatMessage> messagePayload = messageService.composeMessagePayload(previousMessages, processedNewUserMessage, persona);

        webSocketService.sendMessage(
                new WSChatStatusMessage(request.sessionId(), "Thinking...")
        );

        return submit(session.getId(), messagePayload, persona);
//        return new SubmitResult(session, isFirstSubmitInSession, newUserMessage, processedNewUserMessage, response);
    }

    private SseEmitter submit(UUID sessionId, List<OpenAIChatMessage> messages, PersonaEntity persona) {
        Map<String, String> properties = mapToGptProperties(persona.getProperties());

        try {
            OpenAIChatRequest request = new OpenAIChatRequest();
            request.setMessages(messages);

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


            List<RequestFunction> requestFunctions = persona.getRequestFunctions();
            if (!requestFunctions.isEmpty()) {
                request.setFunctions(functionRepository.getByRequestFunctions(requestFunctions));
            }

            SseEmitter emitter = new SseEmitter();
            openAIChatService.stream(request, emitter, message -> {
                handleResultMessage(sessionId, message);
            });

            return emitter;
        } catch (Exception e) {
            LOGGER.error("Error while submitting chat request.", e);
        }

        return null;
    }

    private void handleResultMessage(UUID sessionId, OpenAIChatMessage message) {
        sessionService.saveMessage(sessionId, message, ChatMessageType.UNPROCESSED);

        webSocketService.sendMessage(
                new WSChatStatusMessage(sessionId, "")
        );
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
}
