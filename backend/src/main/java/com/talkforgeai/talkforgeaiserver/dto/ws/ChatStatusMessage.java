package com.talkforgeai.talkforgeaiserver.dto.ws;

import java.util.UUID;

public class ChatStatusMessage extends WebsocketMessage {

    private String status;

    public ChatStatusMessage(UUID sessionId, String status) {
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
