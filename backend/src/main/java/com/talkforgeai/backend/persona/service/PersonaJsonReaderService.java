package com.talkforgeai.backend.persona.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.backend.persona.domain.PersonaEntity;
import com.talkforgeai.backend.persona.domain.PropertyEntity;
import com.talkforgeai.backend.storage.FileStorageService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PersonaJsonReaderService {
    public static final Logger logger = LoggerFactory.getLogger(PersonaJsonReaderService.class);
    private final ResourcePatternResolver resourcePatternResolver;

    private final FileStorageService fileStorageService;

    public PersonaJsonReaderService(FileStorageService fileStorageService) {
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
        this.fileStorageService = fileStorageService;
    }

    public List<PersonaEntity> readJsonFromDirectory() {
        ObjectMapper mapper = new ObjectMapper();
        List<PersonaEntity> personas = new ArrayList<>();

        try {
            Path personaImportDirectory = fileStorageService.getPersonaImportDirectory();

            // Read persona json files from import folder
            try (DirectoryStream<Path> directoryStream =
                         Files.newDirectoryStream(personaImportDirectory, "*.json")) {
                for (Path path : directoryStream) {
                    logger.info("Reading Persona from " + path.getFileName().toString());

                    InputStream inputStream = Files.newInputStream(path);
                    PersonaEntity persona = getPersonaEntity(mapper, inputStream);
                    personas.add(persona);
                }
            }

            // Read persona json files from classpath:persona-import/
            Resource[] resources = resourcePatternResolver.getResources("classpath:persona-import/*.json");
            for (Resource resource : resources) {
                logger.info("Reading Persona from " + resource.getFilename());

                InputStream inputStream = resource.getInputStream();
                PersonaEntity persona = getPersonaEntity(mapper, inputStream);
                personas.add(persona);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return personas;
    }

    @NotNull
    private PersonaEntity getPersonaEntity(ObjectMapper mapper, InputStream inputStream) throws IOException {
        PersonaEntity persona = mapper.readValue(inputStream, PersonaEntity.class);

        Map<String, PropertyEntity> properties = persona.getProperties();
        for (Map.Entry<String, PropertyEntity> entry : properties.entrySet()) {
            PropertyEntity propertyEntity = properties.get(entry.getKey());
            propertyEntity.setPropertyKey(entry.getKey());
        }
        return persona;
    }
}
