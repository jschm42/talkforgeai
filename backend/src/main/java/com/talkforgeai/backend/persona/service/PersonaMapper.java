/*
 * Copyright (c) 2023 Jean Schmitz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.talkforgeai.backend.persona.service;

import com.talkforgeai.backend.persona.domain.PersonaEntity;
import com.talkforgeai.backend.persona.domain.PersonaPropertyValue;
import com.talkforgeai.backend.persona.dto.PersonaDto;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.talkforgeai.backend.persona.service.PersonaProperties.values;
import static java.util.Objects.requireNonNullElse;

@Component
public class PersonaMapper {
    public PersonaDto mapPersonaResponse(PersonaEntity personaEntity) {
        return new PersonaDto(
                personaEntity.getId(),
                personaEntity.getName(),
                personaEntity.getDescription(),
                personaEntity.getBackground(),
                personaEntity.getPersonality(),
                personaEntity.getRequestFunctions(),
                "/api/v1/persona/image/" + personaEntity.getImagePath(),
                personaEntity.getImagePath(),
                mapEntityProperties(personaEntity.getProperties())
        );
    }

    public Map<String, String> mapEntityProperties(Map<String, PersonaPropertyValue> properties) {
        Map<String, String> mappedProperties = new HashMap<>();

        Arrays.stream(values()).forEach(property -> {
            PersonaPropertyValue propertyValue = properties.get(property.getKey());
            if (propertyValue != null) {
                mappedProperties.put(
                        property.getKey(),
                        requireNonNullElse(propertyValue.getPropertyValue(), property.getKey())
                );
            }
        });

        return mappedProperties;
    }

    public Map<String, PersonaPropertyValue> mapProperties(Map<String, String> properties) {
        Map<String, PersonaPropertyValue> mappedProperties = new HashMap<>();

        Arrays.stream(values()).forEach(property -> {
            String propertyValue = properties.get(property.getKey());
            PersonaPropertyValue personaPropertyValue = new PersonaPropertyValue();
            personaPropertyValue.setPropertyValue(propertyValue);
            mappedProperties.put(property.getKey(), personaPropertyValue);
        });

        return mappedProperties;
    }


    PersonaEntity mapPersonaDto(PersonaDto personaDto) {
        PersonaEntity entity = new PersonaEntity();

        entity.setId(personaDto.personaId());
        entity.setName(personaDto.name());
        entity.setDescription(personaDto.description());
        entity.setBackground(personaDto.background());
        entity.setPersonality(personaDto.personality());
        entity.setRequestFunctions(personaDto.requestFunctions());
        entity.setImagePath(personaDto.imagePath());
        // Map properties

        entity.setProperties(mapProperties(personaDto.properties()));
        return entity;
    }
}
