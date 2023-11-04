/*
 * Copyright (c) 2023 Jean Schmitz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.talkforgeai.backend.chat.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.backend.persona.domain.RequestFunction;
import com.talkforgeai.backend.persona.service.PersonaImportService;
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
    Logger logger = LoggerFactory.getLogger(PersonaImportService.class);

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
