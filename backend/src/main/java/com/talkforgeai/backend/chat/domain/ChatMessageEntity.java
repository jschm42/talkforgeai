/*
 * Copyright (c) 2023 Jean Schmitz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.talkforgeai.backend.chat.domain;

import com.talkforgeai.backend.session.domain.ChatSessionEntity;
import com.talkforgeai.service.openai.dto.OpenAIChatMessage;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "CHAT_MESSAGE")
public class ChatMessageEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    ChatMessageType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    OpenAIChatMessage.Role role;

    @Lob
    @Column(columnDefinition = "CLOB")
    String content;

    String functionName;

    String functionCallName;

    @Lob
    @Column(columnDefinition = "CLOB")
    String functionCallArguments;

    @CreationTimestamp
    Date createdAt;

    @UpdateTimestamp
    Date modifiedAt;

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "chat_session_id", nullable = false)
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public OpenAIChatMessage.Role getRole() {
        return role;
    }

    public void setRole(OpenAIChatMessage.Role role) {
        this.role = role;
    }

    public String getFunctionCallName() {
        return functionCallName;
    }

    public void setFunctionCallName(String functionCallName) {
        this.functionCallName = functionCallName;
    }

    public String getFunctionCallArguments() {
        return functionCallArguments;
    }

    public void setFunctionCallArguments(String functionCallArguments) {
        this.functionCallArguments = functionCallArguments;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
}
