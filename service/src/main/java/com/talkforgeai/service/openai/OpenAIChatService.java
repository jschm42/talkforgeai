package com.talkforgeai.service.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.service.openai.dto.OpenAIChatRequest;
import com.talkforgeai.service.openai.dto.OpenAIChatResponse;
import com.talkforgeai.service.properties.OpenAIProperties;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            logger.debug("Sending message: {}", message);

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

                logger.debug("Sending message: {}", message);

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        emitter.completeWithError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            emitter.completeWithError(new RuntimeException("Unexpected code " + response));
                        } else {
                            try (ResponseBody responseBody = response.body()) {
                                try (BufferedReader bufferedReader = new BufferedReader(responseBody.charStream())) {
                                    String line;
                                    while ((line = bufferedReader.readLine()) != null) {
                                        logger.info("LINE: {}", line);
                                        Pattern regEx = Pattern.compile("\"content\":\"(.*?)\"");
                                        Matcher matcher = regEx.matcher(line);

                                        if (matcher.find()) {
                                            logger.info("SENDING: {}", matcher.group(1));
                                            //emitter.send(matcher.group(1), org.springframework.http.MediaType.TEXT_EVENT_STREAM);
                                            emitter.send(matcher.group(1), org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);

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
}
