package com.talkforgeai.talkforgeaiserver.controller;

import com.talkforgeai.talkforgeaiserver.dto.ChatCompletionRequest;
import com.talkforgeai.talkforgeaiserver.dto.ChatCompletionResponse;
import com.talkforgeai.talkforgeaiserver.dto.NewChatSessionRequest;
import com.talkforgeai.talkforgeaiserver.dto.SessionResponse;
import com.talkforgeai.talkforgeaiserver.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/session/{sessionId}")
    public ChatCompletionResponse submit(@PathVariable("sessionId") UUID sessionId,
                                         @RequestBody ChatCompletionRequest request) {
        return chatService.submit(sessionId, request);
    }

    @PostMapping("/create")
    UUID createNewChatSession(@RequestBody NewChatSessionRequest request) {
        return chatService.create(request);
    }

    @GetMapping("/session")
    List<SessionResponse> getChatSessions() {
        return chatService.getSessions();
    }

}
