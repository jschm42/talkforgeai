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
    public static final Logger LOGGER = LoggerFactory.getLogger(ImageDownloadTransformer.class);
    private static final Pattern UrlRegEx = Pattern.compile("<image-prompt>[\\n]?([\\s\\S]*?)[\\n]?</image-prompt>", Pattern.MULTILINE);
    private final OpenAIImageService service;
    String template = """
            %s
            <div class="card shadow">
              <div class="card-body">
                <img src='%s' title='%s'>
              </div>
            </div>
            %s
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

                String formattedContent = template.formatted(
                        NO_LB_MARKER_START,
                        localFilePath,
                        escapeHtml(prompt),
                        NO_LB_MARKER_END
                );

                content = content.replace(fullTag, formattedContent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return content;
    }

    private String downloadImage(String imageUrl, UUID sessionId, Path dataDirectory) throws IOException {
        String fileName = UUID.randomUUID() + "_image.png";
        Path subDirectoryPath = dataDirectory.resolve("chat").resolve(sessionId.toString());
        Path localFilePath = subDirectoryPath.resolve(fileName);

        // Ensure the directory exists and is writable
        if (!Files.exists(subDirectoryPath)) {
            Files.createDirectories(subDirectoryPath);
        }
        if (!Files.isWritable(subDirectoryPath)) {
            throw new IOException("Directory is not writable: " + subDirectoryPath);
        }

        LOGGER.info("Downloading image {}...", imageUrl);

        try {
            URI uri = URI.create(imageUrl);
            try (InputStream in = uri.toURL().openStream()) {
                Files.copy(in, localFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            LOGGER.error("Failed to download image: {}", imageUrl);
            throw ex;
        }

        return "/api/v1/session/" + sessionId + "/" + fileName;
    }
}
