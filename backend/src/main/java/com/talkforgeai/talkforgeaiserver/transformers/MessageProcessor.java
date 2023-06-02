package com.talkforgeai.talkforgeaiserver.transformers;

import com.theokanning.openai.completion.chat.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageProcessor {
    Logger logger = LoggerFactory.getLogger(MessageProcessor.class);
    List<Transformer> transformers = new ArrayList<>();

    public MessageProcessor() {
        transformers.add(new CodeBlockTransformer());
    }

    public ChatMessage transform(ChatMessage message) {
        String processedContent = message.getContent();

        for (Transformer t : transformers) {
            logger.info("Transforming with " + t.getClass().getName() + "...");
            processedContent = t.process(processedContent);
        }

        logger.info("Transformation done.");
        return new ChatMessage(message.getRole(), processedContent);
    }

}
