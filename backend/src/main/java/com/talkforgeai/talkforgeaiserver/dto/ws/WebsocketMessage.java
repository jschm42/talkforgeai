package com.talkforgeai.talkforgeaiserver.dto.ws;

import java.util.UUID;

public class WebsocketMessage {
    private UUID sessionId;

    private WebsocketMessageType type;

    public WebsocketMessage(UUID sessionId, WebsocketMessageType type) {
        this.sessionId = sessionId;
        this.type = type;
    }

    public WebsocketMessageType getType() {
        return type;
    }

    public void setType(WebsocketMessageType type) {
        this.type = type;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }
}
