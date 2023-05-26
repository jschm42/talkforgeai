package com.talkforgeai.talkforgeaiserver.repository;

import com.talkforgeai.talkforgeaiserver.domain.PersonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonaRepository extends JpaRepository<PersonaEntity, UUID> {
    Optional<PersonaEntity> findByName(String name);
}
