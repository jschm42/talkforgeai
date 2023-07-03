package com.talkforgeai.backend.service;

import com.talkforgeai.backend.domain.PersonaEntity;
import com.talkforgeai.backend.dto.PersonaResponse;
import com.talkforgeai.backend.repository.PersonaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final FileStorageService fileStorageService;

    public PersonaService(PersonaRepository personaRepository, FileStorageService fileStorageService) {
        this.personaRepository = personaRepository;
        this.fileStorageService = fileStorageService;
    }

    public PersonaResponse mapPersonaResponse(PersonaEntity personaEntity) {
        return new PersonaResponse(
                personaEntity.getId(),
                personaEntity.getName(),
                personaEntity.getDescription(),
                "/api/v1/persona/image/" + personaEntity.getImagePath()
        );
    }

    public List<PersonaResponse> getAllPersona() {
        return getPersonaResponse(personaRepository.findAll());
    }

    public Optional<PersonaEntity> getPersonaById(UUID personaId) {
        return personaRepository.findById(personaId);
    }

    public Optional<PersonaEntity> getPersonaByName(String personaName) {
        return personaRepository.findByName(personaName);
    }

    public List<PersonaResponse> getPersonaResponse(List<PersonaEntity> personaEntities) {
        return personaEntities.stream().map(this::mapPersonaResponse).toList();
    }

}
