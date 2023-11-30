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

package com.talkforgeai.service.openai.image;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.service.openai.image.dto.OpenAIImageRequest;
import com.talkforgeai.service.openai.image.dto.OpenAIImageResponse;
import com.talkforgeai.service.properties.OpenAIProperties;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OpenAIImageService {

  public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

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

  }
}
