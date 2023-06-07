package com.talkforgeai.talkforgeaiserver;

import com.talkforgeai.talkforgeaiserver.domain.PersonaEntity;
import com.talkforgeai.talkforgeaiserver.domain.PropertyCategory;
import com.talkforgeai.talkforgeaiserver.domain.PropertyEntity;
import com.talkforgeai.talkforgeaiserver.domain.PropertyType;
import com.talkforgeai.talkforgeaiserver.dto.PropertyKeys;
import com.talkforgeai.talkforgeaiserver.repository.PersonaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.talkforgeai.talkforgeaiserver.domain.PropertyCategory.CHATGPT;
import static com.talkforgeai.talkforgeaiserver.domain.PropertyCategory.ELEVENLABS;
import static com.talkforgeai.talkforgeaiserver.domain.PropertyType.*;

@Configuration
public class InitDatabaseConfiguration {
    Logger logger = LoggerFactory.getLogger(InitDatabaseConfiguration.class);

    @Bean
    CommandLineRunner initDatabase(PersonaRepository personaRepository) {
        return args -> {
            // Check if default entities exist
            if (personaRepository.count() == 0) {
                logger.info("No default entities found. Creating default entities");

                PersonaEntity personaChatBot = createPersonaEntity(
                        "Chat-Bot",
                        "The friendly chat bot that will answer your questions",
                        "Add a smiley to the end of every paragraph, covering the current mood.",
                        "chat-bot.png"
                );

                PersonaEntity personaYoda = createPersonaEntity(
                        "Yoda",
                        "Yoda the jedi master",
                        "Stay in the role of master Yoda. Use the force to answer the questions.",
                        "yoda.png"
                );

                personaRepository.save(personaChatBot);
                personaRepository.save(personaYoda);
            }
        };
    }

    private PersonaEntity createPersonaEntity(String name, String description, String system, String imagePath) {
        PersonaEntity personaEntity = new PersonaEntity();
        personaEntity.setName(name);
        personaEntity.setDescription(description);
        personaEntity.setSystem(system);
        personaEntity.setImagePath(imagePath);
        personaEntity.setCreatedOn(new Date());
        personaEntity.getProperties().putAll(createProperties());
        return personaEntity;
    }

    private Map<String, PropertyEntity> createProperties() {
        Map<String, PropertyEntity> map = new HashMap<>();

        map.put(PropertyKeys.CHATGPT_MODEL,
                createProperty(PropertyKeys.CHATGPT_MODEL, "gpt-3.5-turbo", STRING, CHATGPT));
        map.put(PropertyKeys.CHATGPT_MAX_TOKENS,
                createProperty(PropertyKeys.CHATGPT_MAX_TOKENS, "2048", NUMBER, CHATGPT));
        map.put(PropertyKeys.CHATGPT_TEMPERATURE,
                createProperty(PropertyKeys.CHATGPT_TEMPERATURE, "0.7", DOUBLE, CHATGPT));
        map.put(PropertyKeys.CHATGPT_TOP_P,
                createProperty(PropertyKeys.CHATGPT_TOP_P, "1.0", DOUBLE, CHATGPT));
        map.put(PropertyKeys.CHATGPT_PRESENCE_PENALTY,
                createProperty(PropertyKeys.CHATGPT_PRESENCE_PENALTY, "0", DOUBLE, CHATGPT));
        map.put(PropertyKeys.CHATGPT_FREQUENCY_PENALTY,
                createProperty(PropertyKeys.CHATGPT_FREQUENCY_PENALTY, "0", DOUBLE, CHATGPT));

        map.put(PropertyKeys.ELEVENLABS_VOICEID,
                createProperty(PropertyKeys.ELEVENLABS_VOICEID, "21m00Tcm4TlvDq8ikWAM", STRING, ELEVENLABS));

        return map;
    }

    private PropertyEntity createProperty(String key, String value, PropertyType type, PropertyCategory category) {
        PropertyEntity propertyEntity = new PropertyEntity();
        propertyEntity.setPropertyKey(key);
        propertyEntity.setProperyValue(value);
        propertyEntity.setType(type);
        propertyEntity.setCategory(category);
        return propertyEntity;
    }
}
