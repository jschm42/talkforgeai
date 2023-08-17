package com.talkforgeai.backend.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    public static final String TALK_FORGE_DIR = ".talkforgeai";
    public static final Logger LOGGER = LoggerFactory.getLogger(FileStorageService.class);
    private final String fileBasePath = "src/main/resources/static/persona";
    private final ResourceLoader resourceLoader;

    public FileStorageService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
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

    public void copyImagesToHomeDirectory() {
        Path personaImportDirectory = getPersonaImportDirectory();

        // Copy images from the personaImportDirectory
        try (var stream = Files.walk(personaImportDirectory)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".png") || path.toString().endsWith(".jpg"))
                    .forEach(imagePath -> {
                        extracted(imagePath);
                    });
        } catch (IOException e) {
            LOGGER.error("Failed to access persona import directory.", e);
        }

        // Get the resource for the static folder
        Resource staticResource = resourceLoader.getResource("classpath:persona-import/");

        try {
            // Get the absolute path of the static folder
            String staticPath = staticResource.getFile().getAbsolutePath();

            // Copy each image to the user's home directory
            try (var stream = Files.walk(Path.of(staticPath))) {
                stream.filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".png") || path.toString().endsWith(".jpg"))
                        .forEach(imagePath -> {
                            extracted(imagePath);
                        });
            }
        } catch (IOException e) {
            LOGGER.error("Failed to access persona resources.", e);
        }

        LOGGER.info("Images copied successfully.");
    }

    private void extracted(Path imagePath) {
        try {
            Path targetPath = getPersonaDirectory().resolve(imagePath.getFileName());
            Files.copy(imagePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.error("Failed to copy image {}", imagePath, e);
        }
    }
}
