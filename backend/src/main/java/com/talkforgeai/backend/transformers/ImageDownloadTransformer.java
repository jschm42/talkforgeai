package com.talkforgeai.backend.transformers;

import com.talkforgeai.backend.transformers.dto.TransformerContext;
import com.talkforgeai.service.openai.OpenAIImageService;
import com.talkforgeai.service.openai.dto.OpenAIImageRequest;
import com.talkforgeai.service.openai.dto.OpenAIImageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ImageDownloadTransformer implements Transformer {
    private static final Pattern UrlRegEx = Pattern.compile("<image-prompt>[\\n]?([\\s\\S]*?)[\\n]?</image-prompt>", Pattern.MULTILINE);
    private final OpenAIImageService service;
    Logger logger = LoggerFactory.getLogger(ImageDownloadTransformer.class);
    String template = """
            <div class="card shadow">
              <div class="card-body">
                <!--<h5 class="card-title">Card title</h5>-->
                <!--<h6 class="card-subtitle mb-2 text-body-secondary">Card subtitle</h6>-->
                <img src='%s' title='%s'>
              </div>
            </div>
            """;

    public ImageDownloadTransformer(OpenAIImageService service) {
        this.service = service;
    }

    @Override
    public String process(String content, TransformerContext context) {
        Matcher matcher = UrlRegEx.matcher(content);

        while (matcher.find()) {
            String fullTag = matcher.group(0);
            String prompt = matcher.group(1);
            OpenAIImageResponse imageResult = service.submit(new OpenAIImageRequest(prompt));
            try {
                String localFilePath = downloadImage(
                        imageResult.data().get(0).url(),
                        context.sessionId(),
                        context.dataDirectory()
                );

                // Perform your Mustache template replacement here
                content = content.replace(fullTag, template.formatted(localFilePath, prompt));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return content;
    }

    private String downloadImage(String imageUrl, UUID sessionId, Path dataDirectory) throws IOException {
        String fileName = UUID.randomUUID() + ".png";
        Path subDirectoryPath = dataDirectory.resolve("chat").resolve(sessionId.toString());
        Path localFilePath = subDirectoryPath.resolve(fileName);

        // Ensure the directory exists and is writable
        if (!Files.exists(subDirectoryPath)) {
            Files.createDirectories(subDirectoryPath);
        }
        if (!Files.isWritable(subDirectoryPath)) {
            throw new IOException("Directory is not writable: " + subDirectoryPath);
        }

        //RestTemplate restTemplate = new RestTemplate();

        logger.info("Downloading image {}...", imageUrl);
        //ResponseEntity<byte[]> response = restTemplate.getForEntity(imageUrl, byte[].class);
        //Files.write(localFilePath, Objects.requireNonNull(response.getBody()));

        try {
            URI uri = URI.create(imageUrl);
            try (InputStream in = uri.toURL().openStream()) {
                Files.copy(in, localFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "/api/v1/chat/session/" + sessionId + "/" + fileName;
    }
}
