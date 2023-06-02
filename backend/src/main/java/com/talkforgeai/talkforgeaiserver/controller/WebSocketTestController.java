package com.talkforgeai.talkforgeaiserver.controller;

import com.talkforgeai.talkforgeaiserver.service.WebSocketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class WebSocketTestController {

    private final WebSocketService webSocketService;

    public WebSocketTestController(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @GetMapping("/send")
    public void sendTestMessage() {
        webSocketService.sendMessage("/topic/messages", "Hallo Welt!");
    }
}
