package com.talkforgeai.talkforgeaiserver.service;

import com.talkforgeai.talkforgeaiserver.domain.PersonaEntity;
import com.talkforgeai.talkforgeaiserver.dto.PersonaResponse;
import com.talkforgeai.talkforgeaiserver.repository.PersonaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;

    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    private PersonaResponse mapPersonaResponse(PersonaEntity personaEntity) {
        String imgPath = "";
        if (personaEntity.getImagePath() != null) {
            imgPath = "http://localhost:8090/persona/images/%s".formatted(personaEntity.getImagePath());
        }

        return new PersonaResponse(
            personaEntity.getId(),
            personaEntity.getName(),
            personaEntity.getDescription(),
            imgPath
        );
    }

    public List<PersonaResponse> getAllPersona() {
        return getPersonaResponse(personaRepository.findAll());
    }

    public Optional<PersonaEntity> getPersonaById(String personaId) {
        return personaRepository.findById(UUID.fromString(personaId));
    }

    public Optional<PersonaEntity> getPersonaByName(String personaName) {
        return personaRepository.findByName(personaName);
    }

    public List<PersonaResponse> getPersonaResponse(List<PersonaEntity> personaEntities) {
        return personaEntities.stream().map(this::mapPersonaResponse).toList();
    }
}
