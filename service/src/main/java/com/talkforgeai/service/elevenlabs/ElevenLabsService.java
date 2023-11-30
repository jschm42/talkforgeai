/*
 * Copyright (c) 2023 Jean Schmitz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.talkforgeai.service.elevenlabs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsApiRequest;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsModel;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsRequest;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsVoicesResponse;
import com.talkforgeai.service.properties.ElevenlabsProperties;
import java.io.IOException;
import java.util.List;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
        return objectMapper.readValue(responseString, objectMapper.getTypeFactory()
            .constructCollectionType(List.class, ElevenLabsModel.class));
      } else {
        throw new ElevenLabsException("Error fetching ElevenLabs models.");
      }
    } catch (IOException e) {
      throw new ElevenLabsException("Error fetching ElevenLabs models.", e);
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
        throw new ElevenLabsException("Error fetching ElevenLabs models.");
      }
    } catch (IOException e) {
      throw new ElevenLabsException("Error fetching ElevenLabs models.", e);
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
          throw new ElevenLabsException("Error fetching audio stream.");
        }
      } catch (IOException e) {
        throw new ElevenLabsException("Error fetching audio stream.", e);
      }

    } catch (JsonProcessingException e) {
      throw new ElevenLabsException("Error processing JSON.", e);
    }
  }

  @NotNull
  private Headers.Builder createHeaderBuilder() {
    return new Headers.Builder()
        .add("Content-Type", "application/json")
        .add("xi-api-key", properties.apiKey());
  }


}
