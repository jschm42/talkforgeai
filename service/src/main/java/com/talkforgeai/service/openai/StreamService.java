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

import com.talkforgeai.service.openai.dto.OpenAIChatStreamResponse;
import com.talkforgeai.service.openai.dto.StreamRunCreateRequest;
import com.talkforgeai.service.openai.exception.OpenAIException;
import com.talkforgeai.service.properties.OpenAIProperties;
import com.theokanning.openai.runs.RunCreateRequest;
import okhttp3.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StreamService {

  public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
  public static final Logger LOGGER = LoggerFactory.getLogger(StreamService.class);
  private final OpenAIProperties openAIProperties;

  private final WebClient webClient;

  @Value("${server.servlet.async.timeout:10000}")
  private long asyncTimeout;

  public StreamService(OpenAIProperties openAIProperties,
      Builder webClientBuilder) {
    this.openAIProperties = openAIProperties;
    this.webClient = webClientBuilder.build();
  }

  public Flux<ServerSentEvent<OpenAIChatStreamResponse>> stream(
      String threadId,
      RunCreateRequest request) {
    LOGGER.info("Setting async timeout to {}", asyncTimeout);

    StreamRunCreateRequest streamRunCreateRequest = new StreamRunCreateRequest();
    streamRunCreateRequest.setModel(request.getModel());
    streamRunCreateRequest.setAssistantId(request.getAssistantId());
    streamRunCreateRequest.setInstructions(request.getInstructions());
    streamRunCreateRequest.setMetadata(request.getMetadata());
    streamRunCreateRequest.setTools(request.getTools());
    streamRunCreateRequest.setStream(true);

    //String uri = openAIProperties.chatUrl();
    String uri = "https://api.openai.com/v1/threads/" + threadId + "/runs";
    HttpHeaders headers = new HttpHeaders();

    if (openAIProperties.usePostman()) {
      uri = openAIProperties.postmanChatUrl();
      headers.add("x-api-key", openAIProperties.postmanApiKey());
      headers.add("x-mock-response-id", openAIProperties.postmanRequestId());
    } else {
      headers.add("Authorization", "Bearer " + openAIProperties.apiKey());
      headers.add("OpenAI-Beta", "assistants=v1");
    }

    LOGGER.info("Chat Stream Request Headers: {}", headers);

    return webClient.post()
        .uri(uri)
        .headers(httpHeaders -> {
          httpHeaders.addAll(headers);
        })
        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
        .accept(org.springframework.http.MediaType.TEXT_EVENT_STREAM)
        .bodyValue(streamRunCreateRequest)
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
        ).bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<OpenAIChatStreamResponse>>() {
        })
        .mapNotNull(sseEvent -> {

          if ("done".equals(sseEvent.event())) {
            return ServerSentEvent.builder(sseEvent.data())
                .event("done")
                .build();
          } else if ("thread.message.delta".equals(sseEvent.event())) {
            return ServerSentEvent.builder(sseEvent.data())
                .event("thread.message.delta")
                .build();
          } else {
            LOGGER.warn("Unknown event type: {}", sseEvent.event());
            return null;
          }
        })
        .doOnError(throwable -> {
          if (throwable instanceof OpenAIException oe) {
            LOGGER.error("Error from OpenAI.", oe);
          } else {
            LOGGER.error("Error while streaming.", throwable);
          }
        })
        .doOnComplete(() -> {
          LOGGER.info("Stream completed.");
        });
  }


}
