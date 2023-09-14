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

    @Value("${TALKFORGE_DATADIR:}")
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
