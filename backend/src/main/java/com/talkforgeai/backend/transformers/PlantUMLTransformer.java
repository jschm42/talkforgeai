package com.talkforgeai.backend.transformers;

import com.talkforgeai.backend.transformers.dto.TransformerContext;
import com.talkforgeai.service.plantuml.PlantUMLService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PlantUMLTransformer implements Transformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlantUMLTransformer.class);
    private static final Pattern UrlRegEx = Pattern.compile("```([a-z]*)\n@startuml\n([\\s\\S]*?)```", Pattern.MULTILINE);
    final String template = """
            <div class="card shadow">
              <div class="card-body">
                <!--<h5 class="card-title">Card title</h5>-->
                <!--<h6 class="card-subtitle mb-2 text-body-secondary">Card subtitle</h6>-->
                <img src='%s' title='%s'>
              </div>
            </div>
            """;
    private final PlantUMLService service;

    public PlantUMLTransformer(PlantUMLService service) {
        this.service = service;
    }

    @Override
    public String process(String content, TransformerContext context) {
        Matcher matcher = UrlRegEx.matcher(content);

        while (matcher.find()) {
            String fullTag = matcher.group(0);
            String lang = matcher.group(1);
            String code = "@startuml\n" + matcher.group(2);

            String fileName = UUID.randomUUID() + "_plantuml.png";
            Path subDirectoryPath = context.dataDirectory().resolve("chat").resolve(context.sessionId().toString());
            Path localFilePath = subDirectoryPath.resolve(fileName);

            // Ensure the directory exists and is writable
            try {
                if (!Files.exists(subDirectoryPath)) {
                    Files.createDirectories(subDirectoryPath);
                }
                if (!Files.isWritable(subDirectoryPath)) {
                    throw new IOException("Directory is not writable: " + subDirectoryPath);
                }
            } catch (IOException ioEx) {
                throw new RuntimeException(ioEx);
            }

            service.generateUmlDiagram(code, localFilePath.toString());

            String imageUrl = "/api/v1/session/" + context.sessionId() + "/" + fileName;

            // Perform your Mustache template replacement here
            content = content.replace(fullTag, template.formatted(imageUrl, code));
        }

        return content;
    }

}
