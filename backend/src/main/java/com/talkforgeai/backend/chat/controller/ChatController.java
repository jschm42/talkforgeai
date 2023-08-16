package com.talkforgeai.backend.chat.controller;

import com.talkforgeai.backend.chat.dto.ChatCompletionRequest;
import com.talkforgeai.backend.chat.dto.ChatCompletionResponse;
import com.talkforgeai.backend.chat.service.ChatService;
import com.talkforgeai.backend.session.dto.NewChatSessionRequest;
import com.talkforgeai.backend.session.service.SessionService;
import com.talkforgeai.service.openai.dto.OpenAIChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;
    private final SessionService sessionService;

    public ChatController(ChatService chatService, SessionService sessionService) {
        this.chatService = chatService;
        this.sessionService = sessionService;
    }

    @PostMapping("/submit")
    public ChatCompletionResponse submit(@RequestBody ChatCompletionRequest request) {
        return chatService.submitChatRequest(request);
    }

    @PostMapping("/submit/function/confirm/{sessionId}")
    public ChatCompletionResponse submitFunctionConfirm(@PathVariable UUID sessionId) {
        return chatService.submitFuncConfirmation(sessionId);
    }

    @GetMapping("/result/{sessionId}")
    OpenAIChatMessage getResult(@PathVariable UUID sessionId) {
        return sessionService.getLastProcessedMessage(sessionId);
    }

    @PostMapping("/create")
    UUID createNewChatSession(@RequestBody NewChatSessionRequest request) {
        return chatService.create(request);
    }

}
