package com.talkforgeai.service.elevenlabs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsApiRequest;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsRequest;
import com.talkforgeai.service.properties.ElevenlabsProperties;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ElevenLabsService {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final String VOICE_ID = "21m00Tcm4TlvDq8ikWAM";

    private final OkHttpClient client;
    private final ElevenlabsProperties properties;
    Logger logger = LoggerFactory.getLogger(ElevenLabsService.class);

    public ElevenLabsService(OkHttpClient client, ElevenlabsProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    public byte[] stream(ElevenLabsRequest ttsRequest) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ElevenLabsApiRequest apiRequest = new ElevenLabsApiRequest(
                    ttsRequest.text(),
                    ttsRequest.modelId(),
                    new ElevenLabsApiRequest.VoiceSettings()
            );

            String message = objectMapper.writeValueAsString(apiRequest);

            Headers headers = new Headers.Builder()
                    .add("xi-api-key", properties.apiKey())
                    .build();


            RequestBody body = RequestBody.create(message, JSON);
            Request request = new Request.Builder()
                    .url(properties.apiUrl() + "/v1/text-to-speech/" + VOICE_ID + "/stream")
                    .headers(headers)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return response.body().bytes();
                } else {
                    throw new RuntimeException("Error fetching audio stream.");
                }
            } catch (IOException e) {
                throw new RuntimeException("Error fetching audio stream.", e);
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
