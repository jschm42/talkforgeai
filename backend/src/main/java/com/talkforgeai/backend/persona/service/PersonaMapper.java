package com.talkforgeai.backend.persona.service;

import com.talkforgeai.backend.persona.domain.PersonaEntity;
import com.talkforgeai.backend.persona.domain.PropertyEntity;
import com.talkforgeai.backend.persona.dto.PersonaResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PersonaMapper {

    public PersonaResponse mapPersonaResponse(PersonaEntity personaEntity) {
        Map<String, String> mappedProperties = new HashMap<>();
        Map<String, PropertyEntity> properties = personaEntity.getProperties();
        for (Map.Entry<String, PropertyEntity> entry : properties.entrySet()) {
            mappedProperties.put(entry.getKey(), entry.getValue().getPropertyValue());
        }

        return new PersonaResponse(
                personaEntity.getId(),
                personaEntity.getName(),
                personaEntity.getDescription(),
                "/api/v1/persona/image/" + personaEntity.getImagePath(),
                mappedProperties
        );
    }

}
