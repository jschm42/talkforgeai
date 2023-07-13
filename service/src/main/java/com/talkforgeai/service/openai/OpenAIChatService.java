package com.talkforgeai.service.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.service.openai.dto.OpenAIChatRequest;
import com.talkforgeai.service.openai.dto.OpenAIChatResponse;
import com.talkforgeai.service.properties.OpenAIProperties;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
public class OpenAIChatService {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OpenAIProperties openAIProperties;
    private final OkHttpClient client;
    Logger logger = LoggerFactory.getLogger(OpenAIChatService.class);

    public OpenAIChatService(OpenAIProperties openAIProperties, OkHttpClient client) {
        this.openAIProperties = openAIProperties;
        this.client = client;
    }

    public OpenAIChatResponse submit(OpenAIChatRequest openAIRequest) {
        ObjectMapper objectMapper = new ObjectMapper();

        String message = null;
        try {
            message = objectMapper.writeValueAsString(openAIRequest);

            RequestBody body = RequestBody.create(message, JSON);
            Request request = new Request.Builder()
                    .url(openAIProperties.chatUrl())
                    .header("Authorization", "Bearer " + openAIProperties.apiKey())
                    .post(body)
                    .build();

            logger.debug("Sending delta: {}", message);

            try (Response response = client.newCall(request).execute()) {

                return objectMapper.readValue(response.body().string(), OpenAIChatResponse.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public SseEmitter stream(OpenAIChatRequest openAIRequest) {
        SseEmitter emitter = new SseEmitter();
        openAIRequest.setStream(true);

        CompletableFuture.runAsync(() -> {

            ObjectMapper objectMapper = new ObjectMapper();

            String message = null;
            try {
                message = objectMapper.writeValueAsString(openAIRequest);

                RequestBody body = RequestBody.create(message, JSON);
                Request request = new Request.Builder()
                        .url(openAIProperties.chatUrl())
                        .header("Authorization", "Bearer " + openAIProperties.apiKey())
                        .post(body)
                        .build();

                logger.debug("Sending delta: {}", message);

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        emitter.completeWithError(e);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            emitter.completeWithError(new RuntimeException("Unexpected code " + response));
                        } else {
                            try (ResponseBody responseBody = response.body()) {
                                try (BufferedReader bufferedReader = new BufferedReader(responseBody.charStream())) {
                                    String line;
                                    while ((line = bufferedReader.readLine()) != null) {
                                        String responseMessageChunk = parseLine(line);
                                        if (responseMessageChunk != null) {
                                            logger.info("SENDING: {}", responseMessageChunk);
                                            emitter.send(responseMessageChunk, org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
                                            try {
                                                Thread.sleep(20);
                                            } catch (InterruptedException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    }
                                }
                            }
                            emitter.complete();
                        }
                    }
                });

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        return emitter;
    }

    private String parseLine(String line) {
        ObjectMapper objectMapper = new ObjectMapper();

        int jsonStartIndex = line.indexOf('{');
        if (jsonStartIndex == -1) {
            return null;
        }

        String jsonContent = line.substring(jsonStartIndex);

        try {
            OpenAIChatResponse openAIChatResponse = objectMapper.readValue(jsonContent, OpenAIChatResponse.class);
            OpenAIChatResponse.ResponseChoice choice = openAIChatResponse.choices().get(0);
            return objectMapper.writeValueAsString(choice);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while parsing chunk line from stream.", e);
        }
    }
}
