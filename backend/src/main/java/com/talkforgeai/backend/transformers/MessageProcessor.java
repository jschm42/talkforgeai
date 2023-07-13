package com.talkforgeai.backend.transformers;

import com.talkforgeai.backend.storage.FileStorageService;
import com.talkforgeai.backend.transformers.dto.TransformerContext;
import com.talkforgeai.service.openai.OpenAIImageService;
import com.talkforgeai.service.openai.dto.OpenAIChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class MessageProcessor {
    private final OpenAIImageService openAIImageService;
    private final FileStorageService fileStorageService;

    Logger logger = LoggerFactory.getLogger(MessageProcessor.class);
    List<Transformer> transformers = new ArrayList<>();

    public MessageProcessor(OpenAIImageService imageService, FileStorageService fileStorageService) {
        this.openAIImageService = imageService;
        this.fileStorageService = fileStorageService;

        transformers.add(new CodeBlockTransformer());
        transformers.add(new NewLineTransformer());
        transformers.add(new ImageDownloadTransformer(openAIImageService));

    }

    public OpenAIChatMessage transform(OpenAIChatMessage message, UUID sessionId) {
        if (message.content() == null || message.content().isEmpty()) {
            return new OpenAIChatMessage(message.role(), "");
        }

        String processedContent = message.content();

        TransformerContext context = new TransformerContext(sessionId, fileStorageService.getDataDirectory());

        for (Transformer t : transformers) {
            logger.info("Transforming with " + t.getClass().getName() + "...");
            processedContent = t.process(processedContent, context);
        }

        logger.info("Transformation done.");
        return new OpenAIChatMessage(message.role(), processedContent);
    }

}
