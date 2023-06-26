package com.talkforgeai.talkforgeaiserver.service;

import com.talkforgeai.talkforgeaiserver.properties.OpenAIProperties;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class WhisperService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WhisperService.class);
    private final FileStorageService fileStorageService;

    private final OpenAIProperties openAIProperties;

    public WhisperService(FileStorageService fileStorageService, OpenAIProperties openAIProperties) {
        this.fileStorageService = fileStorageService;
        this.openAIProperties = openAIProperties;
    }

    public ResponseEntity<String> convert(@RequestParam("file") MultipartFile file) {
        try {
            // Save the file locally
            Path uploadDir = fileStorageService.getDataDirectory().resolve("uploads");

            try {
                Files.createDirectories(uploadDir);
                LOGGER.info("Upload directory created successfully");
            } catch (IOException e) {
                LOGGER.error("Failed to create directory: " + e.getMessage());
            }

            Path path = uploadDir.resolve("audio.wav");


            LOGGER.info("Tansfering upload file to '{}'...", path);
            file.transferTo(path);

            // Send the file to the Whisper API
            String text = callWhisperAPI(path.toFile());

            // Return the result
            return ResponseEntity.ok(text);
        } catch (Exception e) {
            LOGGER.error("Error while calling whisper API", e);
            return ResponseEntity.status(500).body("Error converting voice to text: " + e.getMessage());
        }
    }

    private String callWhisperAPI(File file) {
        final String uri = "https://api.openai.com/v1/audio/transcriptions";

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "audio.wav",
                        RequestBody.create(file, MediaType.parse("audio/wav")))
                .addFormDataPart("model", "whisper-1")
                .build();

        Request request = new Request.Builder()
                .url(uri)
                .header("Authorization", "Bearer " + openAIProperties.apiKey())
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseText = response.body().string();
            LOGGER.info("Extracted audio text: {}", responseText);
            return responseText;
        } catch (IOException e) {
            LOGGER.error("Error while calling whisper API", e);
        }

        return null;
    }
}
