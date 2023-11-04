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

package com.talkforgeai.backend.transformers;

import com.talkforgeai.backend.storage.FileStorageService;
import com.talkforgeai.backend.transformers.dto.TransformerContext;
import com.talkforgeai.service.openai.OpenAIImageService;
import com.talkforgeai.service.openai.dto.OpenAIChatMessage;
import com.talkforgeai.service.plantuml.PlantUMLService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class MessageProcessor {
    private final FileStorageService fileStorageService;

    Logger logger = LoggerFactory.getLogger(MessageProcessor.class);
    List<Transformer> transformers = new ArrayList<>();

    public MessageProcessor(OpenAIImageService imageService, FileStorageService fileStorageService, PlantUMLService plantUMLService) {
        this.fileStorageService = fileStorageService;

        transformers.add(new LaTeXTransformer());
        transformers.add(new PlantUMLTransformer(plantUMLService));
        transformers.add(new CodeBlockTransformer());
        transformers.add(new NewLineTransformer());
        transformers.add(new ImageDownloadTransformer(imageService));
    }

    public OpenAIChatMessage transform(OpenAIChatMessage message, UUID sessionId) {
        if (message.content() == null || message.content().isEmpty()) {
            return new OpenAIChatMessage(message.role(), "");
        }

        String processedContent = message.content();

        TransformerContext context = new TransformerContext(
                sessionId,
                fileStorageService.getPersonaDirectory(),
                fileStorageService.getPersonaImportDirectory(),
                fileStorageService.getChatDirectory()
        );

        for (Transformer t : transformers) {
            logger.info("Transforming with " + t.getClass().getName() + "...");
            processedContent = t.process(processedContent, context);
        }

        logger.info("Transformation done.");
        return new OpenAIChatMessage(message.role(), processedContent);
    }

}
