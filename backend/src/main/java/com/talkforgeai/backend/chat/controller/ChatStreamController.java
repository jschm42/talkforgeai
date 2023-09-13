package com.talkforgeai.backend.chat.controller;

import com.talkforgeai.backend.chat.dto.ChatCompletionRequest;
import com.talkforgeai.backend.chat.service.ChatStreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat/stream")
public class ChatStreamController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatStreamController.class);
    private final ChatStreamService chatStreamService;

    public ChatStreamController(ChatStreamService chatStreamService) {
        this.chatStreamService = chatStreamService;
    }

    @PostMapping("/submit")
    @ResponseBody
    public SseEmitter submit(@RequestBody ChatCompletionRequest request) {
        return chatStreamService.submit(request);
    }
}
