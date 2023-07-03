package com.talkforgeai.backend.websocket.dto;

import java.util.UUID;

public class WSChatStatusMessage extends WebsocketMessage {

    private String status;

    public WSChatStatusMessage(UUID sessionId, String status) {
        super(sessionId, WebsocketMessageType.STATUS);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
