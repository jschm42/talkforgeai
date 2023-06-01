package com.talkforgeai.talkforgeaiserver.service;

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
    private final String fileBasePath = "src/main/resources/static/persona";
    private final ResourceLoader resourceLoader;
    Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    public FileStorageService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Path getDataDirectory() {
        return Paths.get(System.getProperty("user.home"))
                .resolve(TALK_FORGE_DIR)
                .normalize();
    }

    public Path getPersonaDirectory() {
        return getDataDirectory().resolve("persona");
    }


    public void createDataDirectories() {
        try {
            Files.createDirectories(getDataDirectory());
            Files.createDirectories(getPersonaDirectory());

            logger.info("Directories created successfully");
        } catch (IOException e) {
            logger.error("Failed to create directory: " + e.getMessage());
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
        try {
            // Get the resource for the static folder
            Resource staticResource = resourceLoader.getResource("classpath:static/persona/");

            // Get the absolute path of the static folder
            String staticPath = staticResource.getFile().getAbsolutePath();

            // Copy each image to the user's home directory
            try (var stream = Files.walk(Path.of(staticPath))) {
                stream.filter(Files::isRegularFile)
                        .forEach(imagePath -> {
                            try {
                                Path targetPath = getPersonaDirectory().resolve(imagePath.getFileName());
                                //Files.createDirectories(targetPath.getParent());
                                Files.copy(imagePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                System.out.println("Failed to copy image: " + e.getMessage());
                            }
                        });
            }

            System.out.println("Images copied successfully!");
        } catch (IOException e) {
            System.out.println("Failed to copy images: " + e.getMessage());
        }
    }
}
