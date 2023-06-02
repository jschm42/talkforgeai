package com.talkforgeai.talkforgeaiserver.service;

import com.talkforgeai.talkforgeaiserver.domain.ChatMessageType;
import com.talkforgeai.talkforgeaiserver.domain.ChatSessionEntity;
import com.talkforgeai.talkforgeaiserver.domain.PersonaEntity;
import com.talkforgeai.talkforgeaiserver.dto.*;
import com.talkforgeai.talkforgeaiserver.exception.PersonaException;
import com.talkforgeai.talkforgeaiserver.exception.SessionException;
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

    private final SessionService sessionService;

    private final MessageService messageService;
    private final WebSocketService webSocketService;

    public ChatService(OpenAIChatService openAIChatService,
                       SessionService sessionService,
                       PersonaService personaService,
                       MessageService messageService,
                       WebSocketService webSocketService) {
        this.openAIChatService = openAIChatService;
        this.sessionService = sessionService;
        this.personaService = personaService;
        this.messageService = messageService;
        this.webSocketService = webSocketService;
    }

    public UUID create(NewChatSessionRequest request) {
        PersonaEntity persona = personaService.getPersonaById(request.personaId())
                .orElseThrow(() -> new PersonaException("Persona not found: " + request.personaId()));

        ChatSessionEntity session
                = sessionService.create(persona, new ArrayList<>(), new ArrayList<>());

        return session.getId();
    }

    public ChatCompletionResponse submit(ChatCompletionRequest request) {
        ChatSessionEntity session = sessionService.getById(request.sessionId())
                .orElseThrow(() -> new SessionException("Session not found: " + request.sessionId()));

        PersonaEntity persona = session.getPersona();
        List<ChatMessage> previousMessages = getPreviousMessages(session);
        boolean isFirstSubmitInSession = previousMessages.isEmpty();

        ChatMessage newUserMessage = new ChatMessage(ChatMessageRole.USER.value(), request.prompt());
        // TODO Postprocessing of new user message
        ChatMessage processedNewUserMessage = new ChatMessage(ChatMessageRole.USER.value(), request.prompt());

        List<ChatMessage> messagePayload = composeMessagePayload(previousMessages, processedNewUserMessage, persona);

        webSocketService.sendChatRequestStatus(
                new ChatStatusUpdateMessage(request.sessionId(), "Thinking...")
        );
        List<ChatCompletionChoice> choices = openAIChatService.submit(messagePayload);

        List<ChatMessage> responseMessages = choices.stream()
                .map(ChatCompletionChoice::getMessage)
                .toList();

        List<ChatMessage> messagesToSave = new ArrayList<>();
        messagesToSave.add(newUserMessage);
        messagesToSave.addAll(responseMessages);

        List<ChatMessage> processedMessagesToSave = new ArrayList<>();
        // TODO Postprocessing of response assistant messages

        webSocketService.sendChatRequestStatus(
                new ChatStatusUpdateMessage(request.sessionId(), "Processing...")
        );

        List<ChatMessage> processedResponseMessages = new ArrayList<>();
        processedResponseMessages.addAll(responseMessages);

        processedMessagesToSave.add(processedNewUserMessage);
        processedMessagesToSave.addAll(processedResponseMessages);

        if (isFirstSubmitInSession) {
            sessionService.update(request.sessionId(), newUserMessage.getContent(), "<empty>");
        }
        ChatSessionEntity updatedSession
                = sessionService.update(request.sessionId(), messagesToSave, processedMessagesToSave);

        webSocketService.sendChatRequestStatus(
                new ChatStatusUpdateMessage(request.sessionId(), "")
        );
        
        return createResponse(processedResponseMessages, updatedSession);
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
