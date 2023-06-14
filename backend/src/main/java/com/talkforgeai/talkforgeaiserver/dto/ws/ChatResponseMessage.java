package com.talkforgeai.talkforgeaiserver.dto.ws;

import com.talkforgeai.talkforgeaiserver.openai.OpenAIChatMessage;

import java.util.List;
import java.util.UUID;

public class ChatResponseMessage extends WebsocketMessage {

    private List<OpenAIChatMessage> messages;

    public ChatResponseMessage(UUID sessionId, List<OpenAIChatMessage> messages) {
        super(sessionId, WebsocketMessageType.RESPONSE);
        this.messages = messages;
    }


    public List<OpenAIChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<OpenAIChatMessage> messages) {
        this.messages = messages;
    }
}
