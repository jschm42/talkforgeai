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

package com.talkforgeai.backend.memory.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.backend.assistant.domain.AssistantEntity;
import com.talkforgeai.backend.assistant.repository.AssistantRepository;
import com.talkforgeai.backend.memory.dto.MemoryImportDto;
import com.talkforgeai.backend.memory.exceptions.MemoryException;
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
 * Service for importing memory data. Imports data from a json file and stores it in the database.
 */
@Service
public class MemoryImportService {

  public static final Logger LOGGER = LoggerFactory.getLogger(MemoryImportService.class);
  private final FileStorageService fileStorageService;
  private final MemoryService memoryService;
  private final AssistantRepository assistantRepository;

  public MemoryImportService(FileStorageService fileStorageService, MemoryService memoryService,
      AssistantRepository assistantRepository) {
    this.fileStorageService = fileStorageService;
    this.memoryService = memoryService;
    this.assistantRepository = assistantRepository;
  }

  @Transactional
  public void importMemory() {
    LOGGER.info("Importing memory from directory: {}",
        fileStorageService.getMemoryImportDirectory());
    ObjectMapper objectMapper = new ObjectMapper();
    Path importDirectory = fileStorageService.getMemoryImportDirectory();

    try (Stream<Path> paths = Files.list(importDirectory)) {
      paths
          .filter(file -> file.toString().endsWith(".json"))
          .forEach(file -> {
            try {
              List<MemoryImportDto> memoryImportDtos = objectMapper.readValue(file.toFile(),
                  new TypeReference<>() {
                  });
              memoryImportDtos.forEach(memoryImportDto -> {
                try {
                  String assistantId = null;
                  if (memoryImportDto.assistantName() != null && !memoryImportDto.assistantName()
                      .isBlank()) {
                    AssistantEntity byName = assistantRepository.findByName(
                        memoryImportDto.assistantName()).orElseThrow(() -> new MemoryException(
                        "Assistant not found: " + memoryImportDto.assistantName()));
                    assistantId = byName.getId();
                  }

                  memoryService.store(memoryImportDto.content(), assistantId);
                } catch (Exception e) {
                  LOGGER.error("Failed to save memory: {}", memoryImportDto, e);
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
