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

import com.talkforgeai.backend.persona.controller.GenerateImageResponse;
import com.talkforgeai.backend.persona.domain.PersonaEntity;
import com.talkforgeai.backend.persona.dto.PersonaDto;
import com.talkforgeai.backend.persona.dto.PersonaImageUploadResponse;
import com.talkforgeai.backend.persona.repository.PersonaRepository;
import com.talkforgeai.backend.storage.FileStorageService;
import com.talkforgeai.service.openai.OpenAIImageService;
import com.talkforgeai.service.openai.dto.OpenAIImageRequest;
import com.talkforgeai.service.openai.dto.OpenAIImageResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PersonaService {
    public static final Logger LOGGER = LoggerFactory.getLogger(PersonaService.class);
    private final PersonaRepository personaRepository;
    private final PersonaMapper personaMapper;

    private final FileStorageService fileStorageService;

    private final OpenAIImageService openAIImageService;

    public PersonaService(PersonaRepository personaRepository, PersonaMapper personaMapper, FileStorageService fileStorageService, OpenAIImageService openAIImageService) {
        this.personaRepository = personaRepository;
        this.personaMapper = personaMapper;
        this.fileStorageService = fileStorageService;
        this.openAIImageService = openAIImageService;
    }

    public List<PersonaDto> getAllPersona() {
        return getPersonaResponse(personaRepository.findAll());
    }

    public PersonaDto getPersona(UUID personaId) {
        Optional<PersonaEntity> personaById = this.getPersonaById(personaId);

        if (personaById.isPresent()) {
            return personaMapper.mapPersonaResponse(personaById.get());
        }

        throw new IllegalArgumentException("Persona with id " + personaId + " not found");
    }

    public Optional<PersonaEntity> getPersonaById(UUID personaId) {
        return personaRepository.findById(personaId);
    }

    public Optional<PersonaEntity> getPersonaByName(String personaName) {
        return personaRepository.findByName(personaName);
    }

    public List<PersonaDto> getPersonaResponse(List<PersonaEntity> personaEntities) {
        return personaEntities.stream().map(personaMapper::mapPersonaResponse).toList();
    }

    @Transactional
    public void updatePersona(PersonaDto personaDto) {
        LOGGER.info("Updating persona {}", personaDto);
        personaRepository.save(personaMapper.mapPersonaDto(personaDto));
    }

    public PersonaImageUploadResponse uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        try {
            byte[] bytes = file.getBytes();

            String fileEnding = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String filename = UUID.randomUUID() + fileEnding;

            Path path = fileStorageService.getAssistantsDirectory().resolve(filename);
            Files.write(path, bytes);


            return new PersonaImageUploadResponse(filename);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to upload file", e);
        }
    }

    public GenerateImageResponse generateImage(String prompt) throws IOException {
        OpenAIImageRequest request = new OpenAIImageRequest(prompt, 1, "256x256");
        OpenAIImageResponse response = openAIImageService.submit(request);

        return new GenerateImageResponse(downloadImage(response.data().get(0).url()));
    }

    private String downloadImage(String imageUrl) throws IOException {
        String fileName = UUID.randomUUID() + "_image.png";
        Path subDirectoryPath = fileStorageService.getAssistantsDirectory();
        Path localFilePath = subDirectoryPath.resolve(fileName);

        // Ensure the directory exists and is writable
        if (!Files.exists(subDirectoryPath)) {
            Files.createDirectories(subDirectoryPath);
        }
        if (!Files.isWritable(subDirectoryPath)) {
            throw new IOException("Directory is not writable: " + subDirectoryPath);
        }

        LOGGER.info("Downloading image {}...", imageUrl);

        try {
            URI uri = URI.create(imageUrl);
            try (InputStream in = uri.toURL().openStream()) {
                Files.copy(in, localFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            LOGGER.error("Failed to download image: {}", imageUrl);
            throw ex;
        }

        return fileName;
    }

    @Transactional
    public void deletePersona(UUID personaId) {
        LOGGER.info("Deleting persona {}", personaId);
        personaRepository.deleteById(personaId);
    }
}
