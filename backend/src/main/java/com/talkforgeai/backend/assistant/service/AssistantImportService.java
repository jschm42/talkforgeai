/*
 * Copyright (c) 2024 Jean Schmitz.
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

package com.talkforgeai.backend.assistant.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.backend.assistant.dto.AssistantDto;
import com.talkforgeai.backend.storage.FileStorageService;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for importing assistant data. Imports data from a json file and stores it in the
 * database. The format of the JSON file is defined by AssistantDto. Double entries will be ignored
 * with a warning. The service will check the import directory for new files on startup and import
 * them. An import file can consist of multiple assistants.
 */
@Service
public class AssistantImportService {

  public static final Logger LOGGER = LoggerFactory.getLogger(AssistantImportService.class);
  private final FileStorageService fileStorageService;
  private final AssistantSpringService assistantService;

  public AssistantImportService(FileStorageService fileStorageService,
      AssistantSpringService assistantService) {
    this.fileStorageService = fileStorageService;
    this.assistantService = assistantService;
  }

  @Transactional
  public void importAssistants() {
    LOGGER.info("Importing assistants from directory: {}", fileStorageService.getImportDirectory());
    ObjectMapper objectMapper = new ObjectMapper();
    Path importDirectory = fileStorageService.getImportDirectory();

    try (Stream<Path> paths = Files.list(importDirectory)) {
      paths
          .filter(file -> file.toString().endsWith(".json"))
          .forEach(file -> {
            try {
              List<AssistantDto> assistantDtos = objectMapper.readValue(file.toFile(),
                  new TypeReference<>() {
                  });
              assistantDtos.forEach(assistantDto -> {
                try {
                  if (!assistantService.doesAssistantExistByName(assistantDto.name())) {
                    LOGGER.info("Importing assistant: {}", file);

                    // If the imagePath is not empty, we need to copy the image to the assistants directory.
                    if (assistantDto.imagePath() != null && !assistantDto.imagePath().isEmpty()) {
                      Path imagePath = importDirectory.resolve(assistantDto.imagePath());
                      Path targetPath = fileStorageService.getAssistantsDirectory()
                          .resolve(imagePath.getFileName());
                      Files.copy(imagePath, targetPath,
                          java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }

                    assistantService.createAssistant(assistantDto);
                  } else {
                    LOGGER.warn("Assistant already exists: {}", assistantDto.name());
                  }
                } catch (Exception e) {
                  LOGGER.error("Failed to save assistant: {}", assistantDto, e);
                }
              });
            } catch (IOException e) {
              LOGGER.error("Failed to read file: {}", file, e);
            }
          });
    } catch (IOException e) {
      LOGGER.error("Failed to list files in directory: {} ", importDirectory, e);
    }
  }
}
