package com.talkforgeai.backend.websocket.dto;

import java.util.UUID;

public class WebsocketMessage {
    private final UUID messageId;
    private final UUID sessionId;

    private final WebsocketMessageType type;

    public WebsocketMessage(UUID sessionId, WebsocketMessageType type) {
        this.messageId = UUID.randomUUID();
        this.sessionId = sessionId;
        this.type = type;
    }

    public WebsocketMessageType getType() {
        return type;
    }


    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getMessageId() {
        return messageId;
    }
}
