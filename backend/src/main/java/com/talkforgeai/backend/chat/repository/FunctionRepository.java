package com.talkforgeai.backend.chat.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.backend.persona.domain.RequestFunction;
import com.talkforgeai.backend.persona.service.PersonaJsonReaderService;
import com.talkforgeai.service.openai.dto.OpenAIFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class FunctionRepository {

    private final ResourcePatternResolver resourcePatternResolver;
    Logger logger = LoggerFactory.getLogger(PersonaJsonReaderService.class);

    public FunctionRepository() {
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
    }

    public List<OpenAIFunction> getByRequestFunctions(List<RequestFunction> requestFunctions) {
        List<String> requestFuncNames = requestFunctions.stream().map(Enum::name).toList();

        return this.getAll().stream()
                .filter(f -> requestFuncNames.contains(f.name()))
                .toList();
    }

    public List<OpenAIFunction> getAll() {

        try {
            ObjectMapper mapper = new ObjectMapper();
            Resource resource = resourcePatternResolver.getResource("classpath:functions.json");
            logger.info("Reading Persona from " + resource.getFilename());

            InputStream inputStream = resource.getInputStream();

            return mapper.readValue(inputStream, new TypeReference<>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

}
