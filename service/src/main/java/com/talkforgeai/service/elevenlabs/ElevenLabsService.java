package com.talkforgeai.service.elevenlabs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsApiRequest;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsModel;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsRequest;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsVoicesResponse;
import com.talkforgeai.service.properties.ElevenlabsProperties;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ElevenLabsService {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final Logger LOGGER = LoggerFactory.getLogger(ElevenLabsService.class);
    private static final String DEFAULT_VOICE_ID = "21m00Tcm4TlvDq8ikWAM";
    private final OkHttpClient client;
    private final ElevenlabsProperties properties;

    public ElevenLabsService(OkHttpClient client, ElevenlabsProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    public List<ElevenLabsModel> getModels() {
        Headers headers = createHeaderBuilder().build();

        Request request = new Request.Builder()
                .url(properties.apiUrl() + "/v1/models")
                .headers(headers)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseString = response.body().string();
                LOGGER.info("ElevenLabs model response: {}", responseString);
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(responseString, objectMapper.getTypeFactory().constructCollectionType(List.class, ElevenLabsModel.class));
            } else {
                throw new RuntimeException("Error fetching ElevenLabs models.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error fetching ElevenLabs models.", e);
        }
    }

    public ElevenLabsVoicesResponse getVoices() {
        Headers headers = createHeaderBuilder().build();

        Request request = new Request.Builder()
                .url(properties.apiUrl() + "/v1/voices")
                .headers(headers)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseString = response.body().string();
                LOGGER.info("ElevenLabs model response: {}", responseString);
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(responseString, ElevenLabsVoicesResponse.class);
            } else {
                throw new RuntimeException("Error fetching ElevenLabs models.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error fetching ElevenLabs models.", e);
        }
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

            Headers headers = createHeaderBuilder()
                    .add("accept", "audio/mpeg")
                    .build();

            String voiceId = ttsRequest.voiceId() != null ? ttsRequest.voiceId() : DEFAULT_VOICE_ID;
            RequestBody body = RequestBody.create(message, JSON);
            Request request = new Request.Builder()
                    .url(properties.apiUrl() + "/v1/text-to-speech/" + voiceId + "/stream")
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

    @NotNull
    private Headers.Builder createHeaderBuilder() {
        return new Headers.Builder()
                .add("Content-Type", "application/json")
                .add("xi-api-key", properties.apiKey());
    }


}
