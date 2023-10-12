package com.talkforgeai.backend.persona.dto;

import com.talkforgeai.backend.persona.domain.GlobalSystem;
import com.talkforgeai.backend.persona.domain.RequestFunction;

import java.util.List;
import java.util.Map;

public record PersonaImport(String version,
                            String name,
                            String description,
                            List<GlobalSystem> globalSystems,
                            List<RequestFunction> requestFunctions,
                            String system,
                            String imagePath,
                            Map<String, String> properties) {
}
