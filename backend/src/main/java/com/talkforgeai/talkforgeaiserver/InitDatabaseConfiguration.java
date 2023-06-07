package com.talkforgeai.talkforgeaiserver;

import com.talkforgeai.talkforgeaiserver.repository.PersonaRepository;
import com.talkforgeai.talkforgeaiserver.service.PersonaJsonReaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitDatabaseConfiguration {
    private final PersonaJsonReaderService readerService;
    Logger logger = LoggerFactory.getLogger(InitDatabaseConfiguration.class);

    public InitDatabaseConfiguration(PersonaJsonReaderService readerService) {
        this.readerService = readerService;
    }

    @Bean
    CommandLineRunner initDatabase(PersonaRepository personaRepository) {
        return args -> {
            readerService.readJsonFromDirectory().forEach(persona -> {
                if (personaRepository.findByName(persona.getName()).isPresent()) {
                    logger.info("Persona {} already exists. Skipping.", persona.getName());
                } else {
                    logger.info("Importing Persona: {}", persona);
                    personaRepository.save(persona);
                }
            });
        };
    }
}
