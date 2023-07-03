package com.talkforgeai.backend.persona.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.backend.persona.domain.PersonaEntity;
import com.talkforgeai.backend.persona.domain.PropertyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PersonaJsonReaderService {
    private final ResourcePatternResolver resourcePatternResolver;
    Logger logger = LoggerFactory.getLogger(PersonaJsonReaderService.class);

    public PersonaJsonReaderService() {
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
    }

    public List<PersonaEntity> readJsonFromDirectory() {
        ObjectMapper mapper = new ObjectMapper();
        List<PersonaEntity> personas = new ArrayList<>();

        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:persona-import/*.json");
            for (Resource resource : resources) {
                logger.info("Reading Persona from " + resource.getFilename());

                InputStream inputStream = resource.getInputStream();
                PersonaEntity persona = mapper.readValue(inputStream, PersonaEntity.class);

                Map<String, PropertyEntity> properties = persona.getProperties();
                for (Map.Entry<String, PropertyEntity> entry : properties.entrySet()) {
                    PropertyEntity propertyEntity = properties.get(entry.getKey());
                    propertyEntity.setPropertyKey(entry.getKey());
                }

                personas.add(persona);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return personas;
    }
}
