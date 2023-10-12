package com.talkforgeai.backend.persona.service;

import com.talkforgeai.backend.persona.domain.PersonaEntity;
import com.talkforgeai.backend.persona.dto.PersonaDto;
import org.springframework.stereotype.Component;

@Component
public class PersonaMapper {
    public PersonaDto mapPersonaResponse(PersonaEntity personaEntity) {
        return new PersonaDto(
                personaEntity.getId(),
                personaEntity.getName(),
                personaEntity.getDescription(),
                personaEntity.getSystem(),
                personaEntity.getGlobalSystems(),
                personaEntity.getRequestFunctions(),
                "/api/v1/persona/image/" + personaEntity.getImagePath(),
                personaEntity.getImagePath(),
                personaEntity.getProperties()
        );
    }

    PersonaEntity mapPersonaDto(PersonaDto personaDto) {
        PersonaEntity entity = new PersonaEntity();

        entity.setId(personaDto.personaId());
        entity.setName(personaDto.name());
        entity.setDescription(personaDto.description());
        entity.setSystem(personaDto.system());
        entity.setGlobalSystems(personaDto.globalSystems());
        entity.setRequestFunctions(personaDto.requestFunctions());
        entity.setImagePath(personaDto.imagePath());
        entity.setProperties(personaDto.properties());

        return entity;
    }
}
