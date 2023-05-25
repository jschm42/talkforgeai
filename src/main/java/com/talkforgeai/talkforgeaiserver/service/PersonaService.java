package com.talkforgeai.talkforgeaiserver.service;

import com.talkforgeai.talkforgeaiserver.domain.PersonaEntity;
import com.talkforgeai.talkforgeaiserver.repository.PersonaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;

    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    PersonaEntity getPersona(String personaId) {
        return personaRepository.findById(UUID.fromString(personaId))
            .orElseThrow(() -> new PersonaException("Persona with ID " + personaId + " not found"));
    }
}
