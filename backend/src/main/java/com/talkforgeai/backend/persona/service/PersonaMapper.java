package com.talkforgeai.backend.persona.service;

import com.talkforgeai.backend.persona.domain.PersonaEntity;
import com.talkforgeai.backend.persona.dto.PersonaResponse;
import org.springframework.stereotype.Component;

@Component
public class PersonaMapper {

    public PersonaResponse mapPersonaResponse(PersonaEntity personaEntity) {
        return new PersonaResponse(
                personaEntity.getId(),
                personaEntity.getName(),
                personaEntity.getDescription(),
                "/api/v1/persona/image/" + personaEntity.getImagePath()
        );
    }

}
