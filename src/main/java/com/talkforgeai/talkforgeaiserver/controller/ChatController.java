package com.talkforgeai.talkforgeaiserver.controller;

import com.talkforgeai.talkforgeaiserver.service.ChatService;
import com.talkforgeai.talkforgeaiserver.service.dto.ChatCompletionRequest;
import com.talkforgeai.talkforgeaiserver.service.dto.ChatCompletionResponse;
import com.talkforgeai.talkforgeaiserver.service.dto.NewChatSessionRequest;
import org.springframework.web.bind.annotation.*;

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

}
