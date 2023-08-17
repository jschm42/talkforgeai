package com.talkforgeai.backend.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {
    public static final String TALK_FORGE_DIR = ".talkforgeai";
    public static final Logger LOGGER = LoggerFactory.getLogger(FileStorageService.class);
    private final String fileBasePath = "src/main/resources/static/persona";

    public FileStorageService() {
    }

    public Path getDataDirectory() {
        return Paths.get(System.getProperty("user.home"))
                .resolve(TALK_FORGE_DIR)
                .normalize();
    }

    public Path getPersonaImportDirectory() {
        return getDataDirectory().resolve("import/persona");
    }

    public Path getPersonaDirectory() {
        return getDataDirectory().resolve("persona");
    }

    public void createDataDirectories() {
        try {
            Files.createDirectories(getDataDirectory());
            Files.createDirectories(getPersonaDirectory());
            Files.createDirectories(getPersonaImportDirectory());

            LOGGER.info("Directories created successfully");
        } catch (IOException e) {
            LOGGER.error("Failed to create directory: " + e.getMessage());
        }
    }

    public Resource loadAsResource(String filename) {
        try {
            var file = Paths.get(fileBasePath).resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }

    public Resource loadAsFileResource(String filename) {
        var file = Paths.get(fileBasePath).resolve(filename).normalize();
        Resource resource = new FileSystemResource(file);
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("Could not read file: " + filename);
        }
    }


}
