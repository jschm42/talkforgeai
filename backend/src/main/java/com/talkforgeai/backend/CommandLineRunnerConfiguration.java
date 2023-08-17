package com.talkforgeai.backend;

import com.talkforgeai.backend.persona.service.PersonaImportService;
import com.talkforgeai.backend.storage.FileStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandLineRunnerConfiguration {
    private final PersonaImportService personaImportService;

    private final FileStorageService fileStorageService;

    public CommandLineRunnerConfiguration(FileStorageService fileStorageService, PersonaImportService personaImportService) {
        this.fileStorageService = fileStorageService;
        this.personaImportService = personaImportService;
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            fileStorageService.createDataDirectories();
            personaImportService.importPersona();
        };
    }
}
