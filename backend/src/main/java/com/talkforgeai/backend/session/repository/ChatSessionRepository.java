package com.talkforgeai.backend.session.repository;


import com.talkforgeai.backend.session.domain.ChatSessionEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, UUID> {
    List<ChatSessionEntity> getAllByPersonaId(UUID personaId, Sort sort);
}
