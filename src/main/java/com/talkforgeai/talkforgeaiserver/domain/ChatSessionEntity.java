package com.talkforgeai.talkforgeaiserver.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "CHAT_SESSION")
public class ChatSessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @OneToMany(mappedBy = "chatSession", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ChatMessageEntity> chatMessages = new ArrayList<>();

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id", referencedColumnName = "id")
    private PersonaEntity persona;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<ChatMessageEntity> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(List<ChatMessageEntity> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public PersonaEntity getPersona() {
        return persona;
    }

    public void setPersona(PersonaEntity persona) {
        this.persona = persona;
    }
}
