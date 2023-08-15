package com.talkforgeai.backend.persona.service;

import com.talkforgeai.backend.persona.domain.PersonaEntity;
import com.talkforgeai.backend.persona.dto.PersonaResponse;
import com.talkforgeai.backend.persona.repository.PersonaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final PersonaMapper personaMapper;

    public PersonaService(PersonaRepository personaRepository, PersonaMapper personaMapper) {
        this.personaRepository = personaRepository;
        this.personaMapper = personaMapper;
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
        return personaEntities.stream().map(personaMapper::mapPersonaResponse).toList();
    }

}
