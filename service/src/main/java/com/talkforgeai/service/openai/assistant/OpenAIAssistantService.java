/*
 * Copyright (c) 2023-2024 Jean Schmitz.
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
import com.talkforgeai.service.openai.assistant.dto.Assistant;
import com.talkforgeai.service.openai.assistant.dto.AssistantList;
import com.talkforgeai.service.openai.assistant.dto.GptModelList;
import com.talkforgeai.service.openai.assistant.dto.ListRequest;
import com.talkforgeai.service.openai.assistant.dto.Message;
import com.talkforgeai.service.openai.assistant.dto.MessageList;
import com.talkforgeai.service.openai.assistant.dto.PostMessageRequest;
import com.talkforgeai.service.openai.assistant.dto.Run;
import com.talkforgeai.service.openai.assistant.dto.RunConversationRequest;
import com.talkforgeai.service.openai.assistant.dto.Thread;
import com.talkforgeai.service.openai.exception.OpenAIException;
import com.talkforgeai.service.properties.OpenAIProperties;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

/**
 * This service class is responsible for interacting with the OpenAI Assistant API. It provides
 * methods to create, retrieve, list, modify, and delete assistants. It also provides methods to run
 * conversations, create threads, post messages, retrieve and list messages. It interacts with the
 * OpenAI Assistant API using the provided OpenAIProperties and OkHttpClient.
 *
 * @author Jean Schmitz
 */
@Service
public class OpenAIAssistantService {

  public static final Logger LOGGER = LoggerFactory.getLogger(OpenAIAssistantService.class);
  public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
  private final OpenAIProperties openAIProperties;
  private final OkHttpClient client;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public OpenAIAssistantService(OpenAIProperties openAIProperties, OkHttpClient client) {
    this.openAIProperties = openAIProperties;
    this.client = client;
  }

  /**
   * Creates a new assistant with the provided request details.
   *
   * @param createAssistantRequest the details of the assistant to be created
   * @return the created assistant
   */
  public Assistant createAssistant(Assistant createAssistantRequest) {
    String body = objectToJsonString(createAssistantRequest);
    Request request = createPostRequest(body, "/assistants");
    return executeRequest(request, Assistant.class);
  }

  /**
   * Retrieves an assistant with the provided assistant ID.
   *
   * @param assistantId the ID of the assistant to be retrieved
   * @return the retrieved assistant
   */
  public Assistant retrieveAssistant(String assistantId) {
    Request request = createGetRequest("/assistants/" + assistantId);
    return executeRequest(request, Assistant.class);
  }

  /**
   * Lists all assistants based on the provided request details.
   *
   * @param listAssistantsRequest the details of the list request
   * @return the list of assistants
   */
  public AssistantList listAssistants(ListRequest listAssistantsRequest) {
    Request request = createGetRequest("/assistants?" + createListUrlParams(listAssistantsRequest));
    return executeRequest(request, AssistantList.class);
  }

  /**
   * Runs a conversation with the provided thread ID and request details.
   *
   * @param threadId the ID of the thread
   * @param runConversationRequest the details of the run conversation request
   * @return the run conversation
   */
  public Run runConversation(String threadId, RunConversationRequest runConversationRequest) {
    String body = objectToJsonString(runConversationRequest);
    Request request = createPostRequest(body, "/threads/" + threadId + "/runs");
    return executeRequest(request, Run.class);
  }

  /**
   * Creates a new thread.
   *
   * @return the created thread
   */
  public Thread createThread() {
    Request request = createPostRequest("", "/threads");
    return executeRequest(request, Thread.class);
  }

  /**
   * Posts a message with the provided thread ID and request details.
   *
   * @param threadId the ID of the thread
   * @param postMessageRequest the details of the post message request
   * @return the posted message
   */
  public Message postMessage(String threadId, PostMessageRequest postMessageRequest) {
    String body = objectToJsonString(postMessageRequest);
    Request request = createPostRequest(body, "/threads/" + threadId + "/messages");
    return executeRequest(request, Message.class);
  }

  /**
   * Retrieves a run with the provided thread ID and run ID.
   *
   * @param threadId the ID of the thread
   * @param runId the ID of the run
   * @return the retrieved run
   */
  public Run retrieveRun(String threadId, String runId) {
    Request request = createGetRequest("/threads/" + threadId + "/runs/" + runId);
    return executeRequest(request, Run.class);
  }

  /**
   * Lists all messages with the provided thread ID and request details.
   *
   * @param threadId the ID of the thread
   * @param listMessagesRequest the details of the list messages request
   * @return the list of messages
   */
  public MessageList listMessages(String threadId, ListRequest listMessagesRequest) {
    Request request = createGetRequest(
        "/threads/" + threadId + "/messages?" + createListUrlParams(listMessagesRequest));
    return executeRequest(request, MessageList.class);
  }

  /**
   * Retrieves a message with the provided thread ID and message ID.
   *
   * @param threadId the ID of the thread
   * @param messageId the ID of the message
   * @return the retrieved message
   */
  public Message retrieveMessage(String threadId, String messageId) {
    Request request = createGetRequest("/threads/" + threadId + "/messages/" + messageId);
    return executeRequest(request, Message.class);
  }

  /**
   * Retrieves all models.
   *
   * @return the list of models
   */
  public GptModelList retrieveModels() {
    Request request = createGetRequest("/models");
    return executeRequest(request, GptModelList.class);
  }


  /**
   * Modifies an assistant with the provided assistant ID and modified assistant details.
   *
   * @param assistantId the ID of the assistant to be modified
   * @param openAIModifiedAssistant the modified assistant details
   * @return the modified assistant
   */
  public Assistant modifyAssistant(String assistantId, Assistant openAIModifiedAssistant) {
    String body = objectToJsonString(openAIModifiedAssistant);
    Request request = createPostRequest(body, "/assistants/" + assistantId);
    return executeRequest(request, Assistant.class);
  }

  /**
   * Deletes an assistant with the provided assistant ID.
   *
   * @param assistantId the ID of the assistant to be deleted
   */
  public void deleteAssistant(String assistantId) {
    Request request = createDeleteRequest("/assistants/" + assistantId);
    executeRequest(request, Void.class);
  }

  /**
   * Cancels a run with the provided thread ID and run ID.
   *
   * @param threadId the ID of the thread
   * @param runId the ID of the run
   * @return the cancelled run
   */
  public Run cancelRun(String threadId, String runId) {
    Request request = createPostRequest("/threads/" + threadId + "/runs/" + runId + "/cancel");
    return executeRequest(request, Run.class);
  }

  private Headers.Builder createDefaultHeaderBuilder() {
    Headers.Builder headersBuilder = new Headers.Builder();
    headersBuilder.add("Authorization", "Bearer " + openAIProperties.apiKey());
    headersBuilder.add("OpenAI-Beta", "assistants=v1");
    return headersBuilder;
  }

  private String objectToJsonString(Object object) {

    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new OpenAIException("JSON processing failed.", e);
    }
  }

  private <T> T executeRequest(Request request, Class<T> clazz) {
    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new OpenAIException("Request failed with code " + response.code());
      }
      if (response.body() != null) {
        return new ObjectMapper().readValue(response.body().string(), clazz);
      } else {
        throw new OpenAIException("Response body is null.");
      }
    } catch (IOException e) {
      throw new OpenAIException("Message creation failed.", e);
    }
  }

  private Request createPostRequest(String body, String path) {
    return createRequest(HttpMethod.POST.name(), path, body);
  }

  private Request createPostRequest(String path) {
    return createRequest(HttpMethod.POST.name(), path, null);
  }

  private Request createGetRequest(String path) {
    return createRequest(HttpMethod.GET.name(), path, null);
  }

  private Request createDeleteRequest(String path) {
    return createRequest(HttpMethod.DELETE.name(), path, null);
  }

  private Request createRequest(String method, String path, String body) {
    String apiUrl = openAIProperties.apiUrl() + path;

    RequestBody requestBody = null;
    if (body == null && method.equals(HttpMethod.POST.name())) {
      requestBody = RequestBody.create("", null);
    } else if (body != null) {
      requestBody = RequestBody.create(body, JSON);
    }

    return new Request.Builder()
        .url(apiUrl)
        .headers(createDefaultHeaderBuilder().build())
        .method(method, requestBody)
        .build();
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

}
