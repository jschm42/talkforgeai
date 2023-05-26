package com.talkforgeai.talkforgeaiserver.domain;

import com.theokanning.openai.completion.chat.ChatMessageRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "CHAT_MESSAGE")
public class ChatMessageEntity {
    @NotNull
    @Enumerated(EnumType.STRING)
    ChatMessageType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    ChatMessageRole role;

    @NotNull
    @Lob
    @Column(columnDefinition = "CLOB")
    String content;

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "chat_session_id")
    private ChatSessionEntity chatSession;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ChatMessageType getType() {
        return type;
    }

    public void setType(ChatMessageType type) {
        this.type = type;
    }

    public ChatSessionEntity getChatSession() {
        return chatSession;
    }

    public void setChatSession(ChatSessionEntity chatSession) {
        this.chatSession = chatSession;
    }

    public ChatMessageRole getRole() {
        return role;
    }

    public void setRole(ChatMessageRole role) {
        this.role = role;
    }
}
