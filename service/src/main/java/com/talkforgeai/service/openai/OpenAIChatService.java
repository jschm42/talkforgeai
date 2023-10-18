package com.talkforgeai.service.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.service.openai.dto.OpenAIChatRequest;
import com.talkforgeai.service.openai.dto.OpenAIChatResponse;
import com.talkforgeai.service.openai.dto.OpenAIChatStreamResponse;
import com.talkforgeai.service.properties.OpenAIProperties;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Executor;

@Service
public class OpenAIChatService {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final Logger LOGGER = LoggerFactory.getLogger(OpenAIChatService.class);
    private final OpenAIProperties openAIProperties;
    private final OkHttpClient client;

    private final WebClient webClient;

    private final Executor taskExecutor;

    @Value("${server.servlet.async.timeout:10000}")
    private long asyncTimeout;

    public OpenAIChatService(OpenAIProperties openAIProperties,
                             OkHttpClient client, @Qualifier("sseTaskExecutor") Executor taskExecutor,
                             WebClient.Builder webClientBuilder) {
        this.openAIProperties = openAIProperties;
        this.client = client;
        this.taskExecutor = taskExecutor;
        this.webClient = webClientBuilder.build();
    }

    ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (LOGGER.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder("Request: \n");
                //append clientRequest method and url
                clientRequest
                        .headers()
                        .forEach((name, values) -> values.forEach(value -> {
                            /* append header key/value */
                            sb.append(name).append(":").append(value).append("\n");
                        }));
                LOGGER.debug(sb.toString());

                LOGGER.debug("RequestBody: ", clientRequest.body());
            }
            return Mono.just(clientRequest);
        });
    }

    public OpenAIChatResponse submit(OpenAIChatRequest openAIRequest) {
        ObjectMapper objectMapper = new ObjectMapper();

        String message = null;
        try {
            message = objectMapper.writeValueAsString(openAIRequest);
            RequestBody body = RequestBody.create(message, JSON);

            Headers.Builder headersBuilder = new Headers.Builder();
            String apiUrl = openAIProperties.chatUrl();
            headersBuilder.add("Authorization", "Bearer " + openAIProperties.apiKey());

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .headers(headersBuilder.build())
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {

                return objectMapper.readValue(response.body().string(), OpenAIChatResponse.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Flux<ServerSentEvent<OpenAIChatStreamResponse.StreamResponseChoice>> stream(OpenAIChatRequest openAIRequest, ResultCallback resultCallback) {
        LOGGER.info("Setting async timeout to {}", asyncTimeout);
        openAIRequest.setStream(true);

        LOGGER.info("Chat Stream Request Body: {}", openAIRequest.toJSON());

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
                .bodyValue(openAIRequest)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> {
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        // Here, you can parse the errorBody into a more detailed error message or object if needed
                                        return Mono.error(new OpenAIException("Received error from OpenAI", errorBody));
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

                            String content = openAIChatStreamResponse.choices().get(0).delta().content();

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
                        LOGGER.error("Error from OpenAI: {}", oe.getErrorDetail(), oe);
                    } else {
                        LOGGER.error("Error while streaming.", throwable);
                    }
                })
                .doOnComplete(() -> {
                    LOGGER.info("Stream completed.");
                });
    }

    private String choiceToJSON(OpenAIChatStreamResponse.StreamResponseChoice choice) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(choice);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot parse choice.", e);
        }
    }

    private Optional<OpenAIChatStreamResponse.StreamResponseChoice> parseLine(String line) {
        ObjectMapper objectMapper = new ObjectMapper();

        int jsonStartIndex = line.indexOf('{');
        if (jsonStartIndex == -1) {
            return Optional.empty();
        }

        String jsonContent = line.substring(jsonStartIndex);

        try {
            OpenAIChatStreamResponse openAIChatStreamResponse = objectMapper.readValue(jsonContent, OpenAIChatStreamResponse.class);
            OpenAIChatStreamResponse.StreamResponseChoice streamResponseChoice = openAIChatStreamResponse.choices().get(0);
            if (streamResponseChoice.finishReason() == OpenAIChatStreamResponse.StreamResponseChoice.FinishReason.STOP) {
                return Optional.empty();
            }
            return Optional.of(streamResponseChoice);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while parsing chunk line from stream.", e);
        }
    }
}
