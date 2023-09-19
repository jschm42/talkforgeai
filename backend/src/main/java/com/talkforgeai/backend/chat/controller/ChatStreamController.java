package com.talkforgeai.backend.chat.controller;

import com.talkforgeai.backend.chat.dto.ChatCompletionRequest;
import com.talkforgeai.backend.chat.service.ChatStreamService;
import com.talkforgeai.service.openai.dto.OpenAIChatStreamResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

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
    public Flux<ServerSentEvent<OpenAIChatStreamResponse.StreamResponseChoice>> submit(@RequestBody ChatCompletionRequest request) {
        return chatStreamService.submit(request);
    }
}
