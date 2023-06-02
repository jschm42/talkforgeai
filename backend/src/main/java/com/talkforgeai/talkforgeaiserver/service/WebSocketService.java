package com.talkforgeai.talkforgeaiserver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {
    private final SimpMessagingTemplate template;
    Logger logger = LoggerFactory.getLogger(WebSocketService.class);


    public WebSocketService(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void sendMessage(String destination, String message) {
        logger.info("Sending WS Message to " + destination + ": " + message);
        this.template.convertAndSend(destination, message);
    }
}
