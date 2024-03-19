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

package com.talkforgeai.service.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.service.openai.dto.OpenAIChatStreamResponse;
import com.talkforgeai.service.openai.dto.StreamRunCreateRequest;
import com.talkforgeai.service.openai.exception.OpenAIException;
import com.talkforgeai.service.properties.OpenAIProperties;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StreamService {

  public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
  public static final Logger LOGGER = LoggerFactory.getLogger(StreamService.class);
  private final OpenAIProperties openAIProperties;
  private final OkHttpClient client;

  private final WebClient webClient;

  @Value("${server.servlet.async.timeout:10000}")
  private long asyncTimeout;

  public StreamService(OpenAIProperties openAIProperties,
      OkHttpClient client,
      WebClient.Builder webClientBuilder) {
    this.openAIProperties = openAIProperties;
    this.client = client;
    this.webClient = webClientBuilder.build();
  }

  public Flux<ServerSentEvent<OpenAIChatStreamResponse.StreamResponseChoice>> stream(
      StreamRunCreateRequest request) {
    LOGGER.info("Setting async timeout to {}", asyncTimeout);

    String uri = openAIProperties.chatUrl();
    HttpHeaders headers = new HttpHeaders();

    if (openAIProperties.usePostman()) {
      uri = openAIProperties.postmanChatUrl();
      headers.add("x-api-key", openAIProperties.postmanApiKey());
      headers.add("x-mock-response-id", openAIProperties.postmanRequestId());
    } else {
      headers.add("Authorization", "Bearer " + openAIProperties.apiKey());
    }

    LOGGER.info("Chat Stream Request Headers: {}", headers);

    return webClient.post()
        .uri(uri)
        .headers(httpHeaders -> {
          httpHeaders.addAll(headers);
        })
        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
        .accept(org.springframework.http.MediaType.TEXT_EVENT_STREAM)
        .bodyValue(request)
        .retrieve()
        .onStatus(
            HttpStatusCode::is4xxClientError,
            clientResponse -> {
              return clientResponse.bodyToMono(String.class)
                  .flatMap(errorBody -> {
                    // Here, you can parse the errorBody into a more detailed error message or object if needed
                    return Mono.error(new OpenAIException("Received error from OpenAI"));
                  });
            }
        )
        .bodyToFlux(String.class)
        .mapNotNull(chunkJson -> {
          if ("[DONE]".equals(chunkJson)) {
            return null;
          } else {
            ObjectMapper mapper = new ObjectMapper();
            try {
              OpenAIChatStreamResponse openAIChatStreamResponse
                  = mapper.readValue(chunkJson, OpenAIChatStreamResponse.class);

              // FIXME
              String content = "";
              //String content = openAIChatStreamResponse.choices().get(0).delta().content();

              if (content == null) {
                LOGGER.info("Content is null.");
                return null;
              }
              if ("null".equals(content)) {
                LOGGER.info("Content is 'null'.");
                return null;
              }

              return ServerSentEvent.builder(openAIChatStreamResponse.choices().get(0)).build();
            } catch (JsonProcessingException e) {
              throw new RuntimeException(e);
            }
          }
        })
        .doOnError(throwable -> {
          if (throwable instanceof OpenAIException oe) {
            LOGGER.error("Error from OpenAI.", oe);
            //LOGGER.error("Error from OpenAI: {}", oe.getErrorDetail(), oe);
          } else {
            LOGGER.error("Error while streaming.", throwable);
          }
        })
        .doOnComplete(() -> {
          LOGGER.info("Stream completed.");
        });
  }


}
