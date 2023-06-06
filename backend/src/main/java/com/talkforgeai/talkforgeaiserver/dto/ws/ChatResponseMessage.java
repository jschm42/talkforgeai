package com.talkforgeai.talkforgeaiserver.dto.ws;

import com.theokanning.openai.completion.chat.ChatMessage;

import java.util.List;
import java.util.UUID;

public class ChatResponseMessage extends WebsocketMessage {

    private List<ChatMessage> messages;

    public ChatResponseMessage(UUID sessionId, List<ChatMessage> messages) {
        super(sessionId, WebsocketMessageType.RESPONSE);
        this.messages = messages;
    }


    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }
}
