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
import com.talkforgeai.service.openai.assistant.dto.Thread;
import com.talkforgeai.service.openai.assistant.dto.*;
import com.talkforgeai.service.properties.OpenAIProperties;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public Assistant createAssistant(CreateAssistantRequest createAssistantRequest) {
        String body = objectToJsonString(createAssistantRequest);
        Request request = createPostRequest(body, "/assistants");
        return executeRequest(request, Assistant.class);
    }

    public Assistant retrieveAssistant(String assistantId) {
        Request request = createGetRequest("/assistants/" + assistantId);
        return executeRequest(request, Assistant.class);
    }

    public AssistantList listAssistants(ListRequest listAssistantsRequest) {
        Request request = createGetRequest("/assistants?" + createListUrlParams(listAssistantsRequest));
        return executeRequest(request, AssistantList.class);
    }

    private String createListUrlParams(ListRequest listRequest) {
        List<String> params = new ArrayList<>();

        if (listRequest.limit() != null) {
            params.add("limit=" + listRequest.limit());
        }
        if (listRequest.order() != null) {
            params.add("order=" + listRequest.order());
        }
        if (listRequest.before() != null) {
            params.add("before=" + listRequest.before());
        }
        if (listRequest.after() != null) {
            params.add("after=" + listRequest.after());
        }

        return String.join("&", params);
    }

    public Run runConversation(String threadId, RunConversationRequest runConversationRequest) {
        String body = objectToJsonString(runConversationRequest);
        Request request = createPostRequest(body, "/threads/" + threadId + "/runs");
        return executeRequest(request, Run.class);
    }

    public Thread createThread() {
        Request request = createPostRequest("", "/threads");
        return executeRequest(request, Thread.class);
    }

    public Message postMessage(String threadId, PostMessageRequest postMessageRequest) {
        String body = objectToJsonString(postMessageRequest);
        Request request = createPostRequest(body, "/threads/" + threadId + "/messages");
        return executeRequest(request, Message.class);
    }

    public Run retrieveRun(String threadId, String runId) {
        Request request = createGetRequest("/threads/" + threadId + "/runs/" + runId);
        return executeRequest(request, Run.class);
    }

    public MessageList listMessages(String threadId, ListRequest listMessagesRequest) {
        Request request = createGetRequest("/threads/" + threadId + "/messages?" + createListUrlParams(listMessagesRequest));
        return executeRequest(request, MessageList.class);
    }

    public Message retrieveMessage(String threadId, String messageId) {
        Request request = createGetRequest("/threads/" + threadId + "/messages/" + messageId);
        return executeRequest(request, Message.class);
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

    public Assistant modifyAssistant(String assistantId, Assistant openAIModifiedAssistant) {
        String body = objectToJsonString(openAIModifiedAssistant);
        Request request = createPostRequest(body, "/assistants/" + assistantId);
        return executeRequest(request, Assistant.class);
    }
}
