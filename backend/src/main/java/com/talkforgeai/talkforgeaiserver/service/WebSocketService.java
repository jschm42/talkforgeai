package com.talkforgeai.talkforgeaiserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.talkforgeaiserver.dto.ws.WebsocketMessage;
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

    public void sendMessage(WebsocketMessage message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            logger.info("Sending message over websocket: {}", message);
            this.template.convertAndSend("/topic/messages", mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
