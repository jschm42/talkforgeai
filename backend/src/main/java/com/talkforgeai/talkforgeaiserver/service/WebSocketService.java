package com.talkforgeai.talkforgeaiserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.talkforgeaiserver.dto.ChatStatusUpdateMessage;
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

    public void sendChatRequestStatus(ChatStatusUpdateMessage message) {
        logger.info("Sending ChatRequestStatus Message: " + message);
        ObjectMapper mapper = new ObjectMapper();

        try {
            this.template.convertAndSend("/topic/messages", mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
