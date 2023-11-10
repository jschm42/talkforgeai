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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.backend.persona.domain.PersonaEntity;
import com.talkforgeai.backend.persona.domain.PersonaPropertyValue;
import com.talkforgeai.backend.persona.dto.PersonaImport;
import com.talkforgeai.backend.persona.repository.PersonaRepository;
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
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

@Service
public class PersonaImportService {
    public static final Logger LOGGER = LoggerFactory.getLogger(PersonaImportService.class);
    private final ResourcePatternResolver resourcePatternResolver;

    private final FileStorageService fileStorageService;

    private final ResourcePatternResolver resourceResolver;

    private final PersonaRepository personaRepository;

    public PersonaImportService(FileStorageService fileStorageService, PersonaRepository personaRepository, ResourcePatternResolver resourceResolver) {
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
        this.fileStorageService = fileStorageService;
        this.personaRepository = personaRepository;
        this.resourceResolver = resourceResolver;
    }

    private void savePersona(List<PersonaEntity> personas) {
        personas.forEach(personaEntity -> {
            boolean isExistsPersona = personaRepository.existsByName(personaEntity.getName());

            if (isExistsPersona) {
                LOGGER.info("Persona {} exists. Import skipped.", personaEntity.getName());
            } else {
                LOGGER.info("Importing persona {}", personaEntity.getName());
                personaRepository.save(personaEntity);
            }
        });
    }

    private void importPersonaFromResource(ObjectMapper mapper, List<PersonaEntity> personas) throws IOException {
        Resource[] resources = resourcePatternResolver.getResources("classpath:persona-import/*.json");
        for (Resource resource : resources) {
            LOGGER.info("Found persona {}", resource.getFilename());

            InputStream inputStream = resource.getInputStream();
            PersonaEntity persona = getPersonaEntity(inputStream);
            personas.add(persona);
        }
    }

    private void importPersonaFromImportDirectory(Path personaImportDirectory, ObjectMapper mapper, List<PersonaEntity> personas) throws IOException {
        try (DirectoryStream<Path> directoryStream =
                     Files.newDirectoryStream(personaImportDirectory, "*.json")) {
            for (Path path : directoryStream) {
                LOGGER.info("Found persona {}", path.getFileName().toString());

                InputStream inputStream = Files.newInputStream(path);
                PersonaEntity persona = getPersonaEntity(inputStream);
                personas.add(persona);
            }
        }
    }

    private void copyPersonaImagesFromResources() {
        // The prefix "classpath*:" allows resources to be loaded from the classpath, matching the given pattern
        String personaImgPattern = "classpath*:persona-import/*.{jpg,png}";

        try {
            // Get Resource array for the static folder
            Resource[] imageResources = resourceResolver.getResources(personaImgPattern);

            // Iterate each image resource and copy to target destination
            for (Resource imageResource : imageResources) {
                if (imageResource.isReadable()) {
                    try (InputStream resourceStream = imageResource.getInputStream()) {
                        // You need to redefine your copyImage method to accept InputStream
                        String imageName = imageResource.getFilename();
                        copyImage(resourceStream, imageName);
                    } catch (IOException e) {
                        LOGGER.error("Failed to read image resource.", e);
                    }
                }
            }

        } catch (IOException e) {
            LOGGER.error("Failed to access persona resources.", e);
        }
    }

    private void copyImage(Path imagePath) {
        try {
            Path targetPath = fileStorageService.getAssistantsDirectory().resolve(imagePath.getFileName());

            if (!Files.exists(targetPath)) {
                LOGGER.info("Copying image {}", imagePath);
                Files.copy(imagePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                LOGGER.info("Skipping copying image {}. File already exists in the target directory.", imagePath);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to copy image {}", imagePath, e);
        }
    }

    private void copyImage(InputStream imageStream, String imageName) {
        try {
            Path targetPath = fileStorageService.getAssistantsDirectory().resolve(imageName);

            if (!Files.exists(targetPath)) {
                LOGGER.info("Copying image {}", imageName);
                Files.copy(imageStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                LOGGER.info("Skipping copying image {}. File already exists in the target directory.", imageName);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to copy image {}", imageName, e);
        }
    }

    @NotNull
    private PersonaEntity getPersonaEntity(InputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        PersonaImport personaImport = mapper.readValue(inputStream, PersonaImport.class);

        PersonaEntity personaEntity = new PersonaEntity();
        personaEntity.setName(personaImport.name());
        personaEntity.setDescription(personaImport.description());
        personaEntity.setBackground(personaImport.background());
        personaEntity.setPersonality(personaImport.personality());
        personaEntity.setRequestFunctions(personaImport.requestFunctions());
        personaEntity.setImagePath(personaImport.imagePath());

        // Map persona.properties() to Map<String, PersonaPropertyValue>
        Arrays.stream(PersonaProperties.values()).forEach(p -> {
            PersonaPropertyValue personaPropertyValue = new PersonaPropertyValue();

            String value;
            if (personaImport.properties().containsKey(p.getKey())) {
                value = personaImport.properties().get(p.getKey());
            } else {
                value = p.getDefaultValue();
            }

            personaPropertyValue.setPropertyValue(value);
            personaEntity.getProperties().put(p.getKey(), personaPropertyValue);
        });

        return personaEntity;
    }

}
