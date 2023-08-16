package com.talkforgeai.backend.session.controller;

import com.talkforgeai.backend.chat.service.ChatService;
import com.talkforgeai.backend.session.dto.NewChatSessionRequest;
import com.talkforgeai.backend.session.dto.SessionResponse;
import com.talkforgeai.backend.session.dto.UpdateSessionTitleRequest;
import com.talkforgeai.backend.session.service.SessionService;
import com.talkforgeai.backend.storage.FileStorageService;
import com.talkforgeai.service.openai.dto.OpenAIChatMessage;
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
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/session")
public class SessionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionController.class);
    private final ChatService chatService;
    private final FileStorageService fileStorageService;
    private final SessionService sessionService;

    public SessionController(ChatService chatService, FileStorageService fileStorageService, SessionService sessionService) {
        this.chatService = chatService;
        this.fileStorageService = fileStorageService;
        this.sessionService = sessionService;
    }

    @PostMapping("/create")
    UUID createNewChatSession(@RequestBody NewChatSessionRequest request) {
        return chatService.create(request);
    }

    @GetMapping("/{sessionId}")
    SessionResponse getChatSession(@PathVariable UUID sessionId) {
        return sessionService.getSession(sessionId);
    }

    @DeleteMapping("/{sessionId}")
    void deleteSession(@PathVariable UUID sessionId) {
        sessionService.deleteSession(sessionId);
    }

    @GetMapping("/{sessionId}/{filename}")
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

    @GetMapping("/{sessionId}/postprocess/last")
    public ResponseEntity<OpenAIChatMessage> postProcessLastMessage(@PathVariable("sessionId") UUID sessionId) {
        Optional<OpenAIChatMessage> openAIChatMessage = chatService.postProcessLastMessage(sessionId);
        return openAIChatMessage
                .map(message -> new ResponseEntity<>(message, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @GetMapping("/{personaId}/sessions")
    List<SessionResponse> getChatSessions(@PathVariable UUID personaId) {
        return sessionService.getSessions(personaId);
    }

    @PostMapping("/{sessionId}/title")
    void updateSessionTitle(@PathVariable UUID sessionId, @RequestBody UpdateSessionTitleRequest request) {
        sessionService.updateSessionTitle(sessionId, request);
    }

}
