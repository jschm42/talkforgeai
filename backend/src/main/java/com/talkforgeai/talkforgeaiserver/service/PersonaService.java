package com.talkforgeai.talkforgeaiserver.service;

import com.talkforgeai.talkforgeaiserver.domain.PersonaEntity;
import com.talkforgeai.talkforgeaiserver.dto.PersonaResponse;
import com.talkforgeai.talkforgeaiserver.repository.PersonaRepository;
import org.springframework.core.io.Resource;
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

    private PersonaResponse mapPersonaResponse(PersonaEntity personaEntity) {
        String imgUrl = "";
        if (personaEntity.getImagePath() != null) {
            Resource file = fileStorageService.loadAsResource(personaEntity.getImagePath());
            imgUrl = "/persona/" + file.getFilename();
        }

        return new PersonaResponse(
                personaEntity.getId(),
                personaEntity.getName(),
                personaEntity.getDescription(),
                imgUrl
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
