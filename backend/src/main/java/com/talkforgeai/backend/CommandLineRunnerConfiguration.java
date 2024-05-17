/*
 * Copyright (c) 2023-2024 Jean Schmitz.
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

package com.talkforgeai.backend;

import com.talkforgeai.backend.assistant.service.AssistantImportService;
import com.talkforgeai.backend.memory.service.MemoryImportService;
import com.talkforgeai.backend.storage.FileStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandLineRunnerConfiguration {

  private final FileStorageService fileStorageService;
  private final AssistantImportService assistantImportService;
  private final MemoryImportService memoryImportService;

  public CommandLineRunnerConfiguration(FileStorageService fileStorageService,
      AssistantImportService assistantImportService, MemoryImportService memoryImportService) {
    this.fileStorageService = fileStorageService;
    this.assistantImportService = assistantImportService;
    this.memoryImportService = memoryImportService;
  }

  @Bean
  CommandLineRunner commandLineRunner() {
    return args -> {
      fileStorageService.createDataDirectories();
      assistantImportService.importAssistants();
      memoryImportService.importMemory();
    };
  }
}
