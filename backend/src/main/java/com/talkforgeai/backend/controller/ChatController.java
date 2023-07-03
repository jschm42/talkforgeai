package com.talkforgeai.backend.controller;

import com.talkforgeai.backend.dto.ChatCompletionRequest;
import com.talkforgeai.backend.dto.ChatCompletionResponse;
import com.talkforgeai.backend.dto.NewChatSessionRequest;
import com.talkforgeai.backend.dto.SessionResponse;
import com.talkforgeai.backend.openai.dto.OpenAIChatMessage;
import com.talkforgeai.backend.service.ChatService;
import com.talkforgeai.backend.service.FileStorageService;
import com.talkforgeai.backend.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;
    private final FileStorageService fileStorageService;
    private final MessageService messageService;

    public ChatController(ChatService chatService, FileStorageService fileStorageService, MessageService messageService) {
        this.chatService = chatService;
        this.fileStorageService = fileStorageService;
        this.messageService = messageService;
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
        return messageService.getLastProcessedMessage(sessionId);
    }

    @PostMapping("/create")
    UUID createNewChatSession(@RequestBody NewChatSessionRequest request) {
        return chatService.create(request);
    }

    @GetMapping("/session")
    List<SessionResponse> getChatSessions() {
        return chatService.getSessions();
    }

    @GetMapping("/session/{sessionId}")
    SessionResponse getChatSession(@PathVariable UUID sessionId) {
        return chatService.getSession(sessionId);
    }

    @GetMapping("/session/{sessionId}/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String sessionId, @PathVariable String filename) {
        try {
            Path imgFilePath = fileStorageService.getDataDirectory().resolve("chat").resolve(sessionId).resolve(filename);
            Resource resource = new FileSystemResource(imgFilePath);
            byte[] imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
