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

package com.talkforgeai.backend.assistant.service;

import com.talkforgeai.backend.assistant.dto.AssistantDto;
import com.talkforgeai.backend.assistant.dto.AssistantDto.MemoryType;
import com.talkforgeai.backend.assistant.dto.LlmSystem;
import com.talkforgeai.backend.assistant.exception.AssistentException;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.RequestResponseAdvisor;
import org.springframework.ai.chat.client.advisor.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Flux;

@Service
public class UniversalChatService {

  private final int DEFAULT_CHAT_MEMORY_RESPONSE_SIZE = 5;

  @Qualifier("openAiRestClient")
  private final RestClient openAiRestClient;
  private final OpenAiChatModel openAiChatModel;
  private final MistralAiChatModel mistralAiChatModel;
  private final AnthropicChatModel anthropicChatModel;
  private final OllamaChatModel ollamaChatModel;

  @Qualifier("ollamaAiRestClient")
  private final RestClient ollamaAiRestClient;

  @Qualifier("dbVectorStore")
  private final VectorStore dbVectorStore;

  public UniversalChatService(RestClient openAiRestClient,
      OpenAiChatModel openAiChatModel,
      MistralAiChatModel mistralAiChatModel, AnthropicChatModel anthropicChatModel,
      OllamaChatModel ollamaChatModel,
      RestClient ollamaAiRestClient, VectorStore dbVectorStore) {
    this.openAiRestClient = openAiRestClient;
    this.openAiChatModel = openAiChatModel;
    this.mistralAiChatModel = mistralAiChatModel;
    this.anthropicChatModel = anthropicChatModel;
    this.ollamaChatModel = ollamaChatModel;
    this.ollamaAiRestClient = ollamaAiRestClient;

    this.dbVectorStore = dbVectorStore;
  }

  public ChatOptions getPromptOptions(AssistantDto assistantDto,
      List<FunctionCallback> functionCallbacks) {
    switch (assistantDto.system()) {
      case OPENAI -> {
        return getOpenAiOptions(assistantDto, functionCallbacks);
      }
      case MISTRAL -> {
        return getMistralOptions(assistantDto, functionCallbacks);
      }
      case OLLAMA -> {
        return getOllamaOptions(assistantDto);
      }
      case ANSTHROPIC -> {
        return getAnthropicOptions(assistantDto);
      }
      default -> throw new IllegalStateException("Unexpected system: " + assistantDto.system());
    }
  }

  public String printPromptOptions(LlmSystem system, ChatOptions options) {
    StringBuilder printedOptions = new StringBuilder("[");
    printedOptions.append("system=").append(system).append(", ");

    if (options instanceof OpenAiChatOptions openAiChatOptions) {
      printedOptions.append("model=").append(openAiChatOptions.getModel()).append(", ");
      printedOptions.append("topP=").append(openAiChatOptions.getTopP()).append(", ");
      printedOptions.append("n=").append(openAiChatOptions.getN()).append(", ");
      printedOptions.append("seed=").append(openAiChatOptions.getSeed()).append(", ");
      printedOptions.append("frequencePenalty=").append(openAiChatOptions.getFrequencyPenalty())
          .append(", ");
      printedOptions.append("presencePenalty=").append(openAiChatOptions.getPresencePenalty())
          .append(", ");
      printedOptions.append("temperature=").append(openAiChatOptions.getTemperature());
      printedOptions.append("functions=").append(openAiChatOptions.getFunctions());
      printedOptions.append("tools=").append(openAiChatOptions.getTools());
    } else if (options instanceof MistralAiChatOptions mistralAiChatOptions) {
      printedOptions.append("model=").append(mistralAiChatOptions.getModel()).append(", ");
      printedOptions.append("topP=").append(mistralAiChatOptions.getTopP()).append(", ");
      printedOptions.append("temperature=").append(mistralAiChatOptions.getTemperature());
    } else if (options instanceof OllamaOptions ollamaOptions) {
      printedOptions.append("model=").append(ollamaOptions.getModel()).append(", ");
      printedOptions.append("temperature=").append(ollamaOptions.getTemperature()).append(", ");
      printedOptions.append("frequencePenalty=").append(ollamaOptions.getFrequencyPenalty())
          .append(", ");
      printedOptions.append("presencePenalty=").append(ollamaOptions.getPresencePenalty());
    } else if (options instanceof AnthropicChatOptions anthropicChatOptions) {
      printedOptions.append("model=").append(anthropicChatOptions.getModel()).append(", ");
      printedOptions.append("temperature=").append(anthropicChatOptions.getTemperature())
          .append(", ");
      printedOptions.append("topP=").append(anthropicChatOptions.getTopP());
    } else {
      throw new IllegalStateException("Unexpected options: " + options);
    }

    printedOptions.append("]");
    return printedOptions.toString();
  }

  ChatResponse call(LlmSystem system, String prompt, ChatOptions options) {
    return getClient(system)
        .prompt()
        .options(options)
        .user(prompt)
        .call()
        .chatResponse();
  }

  Flux<ChatResponse> stream(AssistantDto assistantDto, List<Message> messages,
      String userMessage, ChatOptions options) {

    List<RequestResponseAdvisor> requestResponseAdvisors = new ArrayList<>();

    if (assistantDto.memory() == MemoryType.HISTORY) {
      requestResponseAdvisors.add(getVectorStoreChatMemoryAdvisor(assistantDto.id()));
    }

    return getClient(assistantDto.system())
        .prompt()
        .advisors(requestResponseAdvisors)
        .options(options)
        .messages(messages)
        .user(userMessage)
        .stream()
        .chatResponse();
  }

  private @NotNull VectorStoreChatMemoryAdvisor getVectorStoreChatMemoryAdvisor(
      String converationId) {
    return new VectorStoreChatMemoryAdvisor(
        dbVectorStore,
        converationId,
        DEFAULT_CHAT_MEMORY_RESPONSE_SIZE
    );
  }

  private ChatClient getClient(LlmSystem system) {
    ChatModel model = switch (system) {
      case OPENAI -> openAiChatModel;
      case MISTRAL -> mistralAiChatModel;
      case OLLAMA -> ollamaChatModel;
      case ANSTHROPIC -> anthropicChatModel;
    };

    return ChatClient.builder(model)
        .build();
  }

  private MistralAiChatOptions getMistralOptions(AssistantDto assistantDto,
      List<FunctionCallback> functionCallbacks) {
    return MistralAiChatOptions.builder()
        .withModel(assistantDto.model())
        .withTopP(
            Float.valueOf(assistantDto.properties().get(AssistantProperties.MODEL_TOP_P.getKey())))
        .withTemperature(
            Float.valueOf(
                assistantDto.properties().get(AssistantProperties.MODEL_TEMPERATURE.getKey())))
        .withFunctionCallbacks(functionCallbacks)
        .build();
  }

  private OpenAiChatOptions getOpenAiOptions(AssistantDto assistantDto,
      List<FunctionCallback> functionCallbacks) {
    Map<String, String> properties = assistantDto.properties();

    return OpenAiChatOptions.builder()
        .withModel(assistantDto.model())
        .withTopP(Float.valueOf(properties.get(AssistantProperties.MODEL_TOP_P.getKey())))
        .withFrequencyPenalty(
            Float.valueOf(properties.get(AssistantProperties.MODEL_FREQUENCY_PENALTY.getKey())))
        .withPresencePenalty(
            Float.valueOf(properties.get(AssistantProperties.MODEL_PRESENCE_PENALTY.getKey())))
        .withTemperature(
            Float.valueOf(properties.get(AssistantProperties.MODEL_TEMPERATURE.getKey())))
        .withFunctionCallbacks(functionCallbacks)
        .build();
  }

  private ChatOptions getOllamaOptions(AssistantDto assistantDto) {
    Map<String, String> properties = assistantDto.properties();

    return OllamaOptions.create()
        .withModel(assistantDto.model())
        .withFrequencyPenalty(
            Float.valueOf(properties.get(AssistantProperties.MODEL_FREQUENCY_PENALTY.getKey())))
        .withPresencePenalty(
            Float.valueOf(properties.get(AssistantProperties.MODEL_PRESENCE_PENALTY.getKey())))
        .withTemperature(
            Float.valueOf(properties.get(AssistantProperties.MODEL_TEMPERATURE.getKey())));
  }

  private ChatOptions getAnthropicOptions(AssistantDto assistantDto) {
    Map<String, String> properties = assistantDto.properties();

    return AnthropicChatOptions.builder()
        .withModel(assistantDto.model())
        .withTemperature(
            Float.valueOf(properties.get(AssistantProperties.MODEL_TEMPERATURE.getKey())))
        .withTopP(Float.valueOf(properties.get(AssistantProperties.MODEL_TOP_P.getKey())))
        .build();
  }

  public List<String> getModels(LlmSystem llmSystem) {
    switch (llmSystem) {
      case OPENAI -> {
        OpenAiModelResponse response = openAiRestClient.get().uri("/v1/models").retrieve()
            .body(OpenAiModelResponse.class);

        if (response == null) {
          throw new AssistentException("Failed to retrieve models from OpenAI");
        }

        return response.data().stream()
            .map(OpenAiModelResponse.OpenAiModelDetail::id)
            .filter(id -> id.startsWith("gpt-"))
            .sorted()
            .toList();
      }
      case MISTRAL -> {
        return Arrays.stream(MistralAiApi.ChatModel.values()).map(MistralAiApi.ChatModel::getValue)
            .toList();
      }
      case OLLAMA -> {
        OllamaAiModelResponse response = ollamaAiRestClient.get().uri("/api/tags").retrieve()
            .body(OllamaAiModelResponse.class);

        if (response == null) {
          throw new AssistentException("Failed to retrieve models from Ollama");
        }

        return response.models().stream().map(OllamaAiModelResponse.OllamaAiModelDetail::name)
            .sorted()
            .toList();
      }
      case ANSTHROPIC -> {
        return Arrays.stream(AnthropicApi.ChatModel.values()).map(AnthropicApi.ChatModel::getValue)
            .toList();
      }
      default -> throw new IllegalStateException("Unexpected system: " + llmSystem);
    }
  }


  record OpenAiModelResponse(String object, List<OpenAiModelDetail> data) {

    record OpenAiModelDetail(String id) {

    }
  }

  record OllamaAiModelResponse(List<OllamaAiModelDetail> models) {

    record OllamaAiModelDetail(String name) {

    }
  }

}
