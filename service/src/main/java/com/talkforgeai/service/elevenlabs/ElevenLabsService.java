/*
 * Copyright (c) 2024 Jean Schmitz.
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
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ElevenLabsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElevenLabsService.class);
  private static final String DEFAULT_VOICE_ID = "21m00Tcm4TlvDq8ikWAM";

  private final RestClient restClient;
  private final ElevenlabsProperties properties;
  private final ObjectMapper objectMapper;

  public ElevenLabsService(ElevenlabsProperties properties, ObjectMapper objectMapper,
      @Qualifier("elevenLabsRestClient") RestClient restClient) {
    this.properties = properties;
    this.objectMapper = objectMapper;
    this.restClient = restClient;
  }

  public List<ElevenLabsModel> getModels() {
    return restClient.get()
        .uri("/v1/models")
        .retrieve()
        .body(new ParameterizedTypeReference<List<ElevenLabsModel>>() {
        });
  }

  public ElevenLabsVoicesResponse getVoices() {
    return restClient.get()
        .uri("/v1/voices")
        .retrieve()
        .body(ElevenLabsVoicesResponse.class);
  }

  public byte[] stream(ElevenLabsRequest ttsRequest) {
    try {
      ElevenLabsApiRequest apiRequest = new ElevenLabsApiRequest(
          ttsRequest.text(),
          ttsRequest.modelId(),
          new ElevenLabsApiRequest.VoiceSettings()
      );

      String voiceId = ttsRequest.voiceId() != null ? ttsRequest.voiceId() : DEFAULT_VOICE_ID;
      String requestBody = objectMapper.writeValueAsString(apiRequest);

      return restClient.post()
          .uri("/v1/text-to-speech/{voiceId}/stream", voiceId)
          .body(requestBody)
          .accept(MediaType.parseMediaType("audio/mpeg"))
          .retrieve()
          .body(byte[].class);

    } catch (JsonProcessingException e) {
      throw new ElevenLabsException("Error processing JSON.", e);
    }
  }
}