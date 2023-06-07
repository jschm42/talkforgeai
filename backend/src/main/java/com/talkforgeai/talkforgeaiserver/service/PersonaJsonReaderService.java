package com.talkforgeai.talkforgeaiserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.talkforgeaiserver.domain.PersonaEntity;
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

                // Map of properties may need additional handling depending on your JSON structure
                // Assuming the properties in JSON are represented as an array of objects
                // Each object has two fields: "propertyKey" and "propertyEntity"
                // Each "propertyEntity" is a JSON object that can be converted to PropertyEntity

//                Map<String, PropertyEntity> properties = persona.getProperties();
//                for (Map.Entry<String, PropertyEntity> entry : properties.entrySet()) {
//                    // Do any additional processing with the PropertyEntity if needed
//                }

                personas.add(persona);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return personas;
    }
}
