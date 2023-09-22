package com.talkforgeai.backend.chat.service;

import com.talkforgeai.backend.chat.exception.ChatException;
import com.talkforgeai.backend.persona.domain.GlobalSystem;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SystemService {

    public static final String IMAGE_GEN_SYSTEM = "You can generate DALL-E 2 prompts, that will be converted to images. Use the following format: " +
            "Place the content between the following tags: <image-prompt></image-prompt>.";

    public static final String PLANTUML_SYSTEM = "Use PlantUML code for clarification. PlantUML code will be transformed to diagram images.";

    public String getContent(GlobalSystem system) {
        Objects.requireNonNull(system, "Global system cannot be null.");

        if (system == GlobalSystem.IMAGE_GEN) {
            return IMAGE_GEN_SYSTEM;
        } else if (system == GlobalSystem.PLANTUML) {
            return PLANTUML_SYSTEM;
        }

        throw new ChatException("Unknown global system: " + system);
    }
}
