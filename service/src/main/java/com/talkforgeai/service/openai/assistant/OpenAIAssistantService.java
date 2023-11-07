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

package com.talkforgeai.service.openai.assistant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.service.openai.OpenAIChatService;
import com.talkforgeai.service.openai.OpenAIException;
import com.talkforgeai.service.openai.assistant.dto.*;
import com.talkforgeai.service.properties.OpenAIProperties;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OpenAIAssistantService {
    public static final Logger LOGGER = LoggerFactory.getLogger(OpenAIChatService.class);
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OpenAIProperties openAIProperties;
    private final OkHttpClient client;

    public OpenAIAssistantService(OpenAIProperties openAIProperties, OkHttpClient client) {
        this.openAIProperties = openAIProperties;
        this.client = client;
    }

    public CreateAssistantResponse createAssistant(CreateAssistantRequest createAssistantRequest) {
        String body = objectToJsonString(createAssistantRequest);
        Request request = createPostRequest(body, "/assistants");
        return executeRequest(request, CreateAssistantResponse.class);
    }

    public ListAssistantResponse listAssistants(ListAssistantsRequest listAssistantsRequest) {
        String params = """
                limit=%s
                &order=%s
                &after=%s
                &before=%s
                """.formatted(listAssistantsRequest.limit(), listAssistantsRequest.order(), listAssistantsRequest.after(), listAssistantsRequest.before());

        Request request = createGetRequest("/assistants?" + params);
        return executeRequest(request, ListAssistantResponse.class);
    }

    public RunConversationResponse runConversation(String threadId, RunConversationRequest runConversationRequest) {
        String body = objectToJsonString(runConversationRequest);
        Request request = createPostRequest(body, "/threads/" + threadId + "/runs");
        return executeRequest(request, RunConversationResponse.class);
    }

    public CreateThreadResponse createThread() {
        Request request = createPostRequest("", "/threads");
        return executeRequest(request, CreateThreadResponse.class);
    }

    public RunConversationResponse postMessage(String threadId, PostMessageRequest postMessageRequest) {
        String body = objectToJsonString(postMessageRequest);
        Request request = createPostRequest(body, "/threads/" + threadId + "/messages");
        return executeRequest(request, RunConversationResponse.class);
    }

    public RetrieveRunResponse retrieveRun(String threadId, String runId) {
        Request request = createGetRequest("/threads/" + threadId + "/runs/" + runId);
        return executeRequest(request, RetrieveRunResponse.class);
    }

    public ListMessageResponse listMessages(String threadId, ListMessagesRequest listMessagesRequest) {
        String params = """
                limit=%s
                &order=%s
                &after=%s
                &before=%s
                """.formatted(listMessagesRequest.limit(), listMessagesRequest.order(), listMessagesRequest.after(), listMessagesRequest.before());

        Request request = createGetRequest("/threads/" + threadId + "/messages?" + params);
        return executeRequest(request, ListMessageResponse.class);
    }

    private Headers.Builder createDefaultHeaderBuilder() {
        Headers.Builder headersBuilder = new Headers.Builder();
        headersBuilder.add("Authorization", "Bearer " + openAIProperties.apiKey());
        headersBuilder.add("OpenAI-Beta", "assistants=v1");
        return headersBuilder;
    }

    private String objectToJsonString(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new OpenAIException("JSON processing failed.", e);
        }
    }

    private <T> T executeRequest(Request request, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();

        try (Response response = client.newCall(request).execute()) {
            return objectMapper.readValue(response.body().string(), clazz);
        } catch (IOException e) {
            throw new OpenAIException("Message creation failed.", e);
        }
    }

    private Request createPostRequest(String body, String path) {
        RequestBody requestBody = RequestBody.create(body, JSON);
        String apiUrl = openAIProperties.apiUrl() + path;

        return new Request.Builder()
                .url(apiUrl)
                .headers(createDefaultHeaderBuilder().build())
                .post(requestBody)
                .build();
    }

    private Request createGetRequest(String path) {
        String apiUrl = openAIProperties.apiUrl() + path;

        return new Request.Builder()
                .url(apiUrl)
                .headers(createDefaultHeaderBuilder().build())
                .get()
                .build();
    }

}
