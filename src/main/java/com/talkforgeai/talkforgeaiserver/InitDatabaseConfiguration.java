package com.talkforgeai.talkforgeaiserver;

import com.talkforgeai.talkforgeaiserver.domain.PersonaEntity;
import com.talkforgeai.talkforgeaiserver.repository.PersonaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitDatabaseConfiguration {
    Logger logger = LoggerFactory.getLogger(InitDatabaseConfiguration.class);

    @Bean
    CommandLineRunner initDatabase(PersonaRepository personaRepository) {
        return args -> {
            // Check if default entities exist
            if (personaRepository.count() == 0) {
                logger.info("No default entities found. Creating default entities");

                // Create default Persona
                PersonaEntity personaDefault = createPersonaEntity("Default", "The default persona");

                PersonaEntity personaChatBot = createPersonaEntity("ChatBot", "The friendly ChatBot");

                personaRepository.save(personaDefault);
                personaRepository.save(personaChatBot);
            }
        };
    }

    private PersonaEntity createPersonaEntity(String name, String description) {
        PersonaEntity personaEntity = new PersonaEntity();
        personaEntity.setName(name);
        personaEntity.setDescription(description);

        return personaEntity;
    }
}
