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

package com.talkforgeai.backend.storage;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    public static final String TALK_FORGE_DIR = ".talkforgeai";
    public static final Logger LOGGER = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${TALKFORGEAI_DATADIR:}")
    private String configDataDirectory;

    private Path dataDirectory;

    public FileStorageService() {
    }

    @PostConstruct
    private void postConstruct() {
        if (configDataDirectory != null && !configDataDirectory.isEmpty()) {
            this.dataDirectory = Path.of(configDataDirectory);

        } else {
            this.dataDirectory = Paths.get(System.getProperty("user.home"))
                    .resolve(TALK_FORGE_DIR)
                    .normalize();
        }

        LOGGER.info("Data directory set to {}", this.dataDirectory);
    }

    public Path getDataDirectory() {
        return this.dataDirectory;
    }

    public Path getPersonaImportDirectory() {
        return getDataDirectory().resolve("import/persona");
    }

    public Path getPersonaDirectory() {
        return getDataDirectory().resolve("persona");
    }

    public Path getChatDirectory() {
        return getDataDirectory().resolve("chat");
    }

    public void createDataDirectories() {
        try {
            Path createdPath = Files.createDirectories(getDataDirectory());
            LOGGER.info("Created data directory {}", createdPath);

            createdPath = Files.createDirectories(getPersonaDirectory());
            LOGGER.info("Created persona directory {}", createdPath);

            createdPath = Files.createDirectories(getPersonaImportDirectory());
            LOGGER.info("Created persona import directory {}", createdPath);

            LOGGER.info("Directories created successfully");
        } catch (IOException e) {
            LOGGER.error("Failed to create directory: " + e.getMessage());
        }
    }

}
