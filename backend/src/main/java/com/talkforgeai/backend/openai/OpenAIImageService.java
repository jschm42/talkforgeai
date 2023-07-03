package com.talkforgeai.backend.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.backend.openai.dto.OpenAIImageRequest;
import com.talkforgeai.backend.openai.dto.OpenAIImageResponse;
import com.talkforgeai.backend.properties.OpenAIProperties;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.talkforgeai.backend.openai.OpenAIChatService.JSON;

@Service
public class OpenAIImageService {
    private final OkHttpClient client;

    private final OpenAIProperties openAIProperties;

    Logger logger = LoggerFactory.getLogger(OpenAIImageService.class);

    public OpenAIImageService(OpenAIProperties openAIProperties, OkHttpClient client) {
        this.openAIProperties = openAIProperties;
        this.client = client;
    }

    public OpenAIImageResponse submit(OpenAIImageRequest imageRequest) {
        ObjectMapper objectMapper = new ObjectMapper();

        String message = null;
        try {
            message = objectMapper.writeValueAsString(imageRequest);

            RequestBody body = RequestBody.create(message, JSON);
            Request request = new Request.Builder()
                    .url(openAIProperties.imageUrl())
                    .header("Authorization", "Bearer " + openAIProperties.apiKey())
                    .post(body)
                    .build();

            logger.debug("Sending image request: {}", message);

            try (Response response = client.newCall(request).execute()) {
                return objectMapper.readValue(response.body().string(), OpenAIImageResponse.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

//
//        CreateImageRequest imageRequest = new CreateImageRequest();
//        imageRequest.setPrompt(imagePrompt);
//        imageRequest.setN(1);
//        imageRequest.setSize("512x512");
//        imageRequest.setResponseFormat("url");
//        return service.createImage(imageRequest);
    }
}
