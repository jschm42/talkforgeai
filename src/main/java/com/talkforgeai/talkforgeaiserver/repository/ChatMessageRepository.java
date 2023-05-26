package com.talkforgeai.talkforgeaiserver.repository;

import com.talkforgeai.talkforgeaiserver.domain.ChatMessageEntity;
import com.talkforgeai.talkforgeaiserver.domain.ChatMessageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, UUID> {
    List<ChatMessageEntity> findAllByChatSessionIdAndType(UUID sessionId, ChatMessageType type);
}
