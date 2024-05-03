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
import com.talkforgeai.backend.assistant.dto.LlmSystem;
import java.util.Map;
import org.springframework.ai.anthropic.AnthropicChatClient;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.StreamingChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mistralai.MistralAiChatClient;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class UniversalChatService {

  private final OpenAiChatClient openAiChatClient;
  private final MistralAiChatClient mistralAiChatClient;
  private final AnthropicChatClient anthropicChatClient;
  private final OllamaChatClient ollamaChatClient;

  public UniversalChatService(OpenAiChatClient openAiChatClient,
      MistralAiChatClient mistralAiChatClient, AnthropicChatClient anthropicChatClient,
      OllamaChatClient ollamaChatClient) {
    this.openAiChatClient = openAiChatClient;
    this.mistralAiChatClient = mistralAiChatClient;
    this.anthropicChatClient = anthropicChatClient;
    this.ollamaChatClient = ollamaChatClient;
  }

  public ChatOptions getPromptOptions(AssistantDto assistantDto) {
    switch (assistantDto.system()) {
      case OPENAI -> {
        return getOpenAiOptions(assistantDto);
      }
      case MISTRAL -> {
        return getMistralOptions(assistantDto);
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

  ChatResponse call(LlmSystem system, Prompt prompt) {
    return getChatClient(system).call(prompt);
  }

  Flux<ChatResponse> stream(LlmSystem system, Prompt prompt) {
    return getStreamingChatClient(system).stream(prompt);
  }

  StreamingChatClient getStreamingChatClient(LlmSystem system) {
    return (StreamingChatClient) getClient(system);
  }

  ChatClient getChatClient(LlmSystem system) {
    return (ChatClient) getClient(system);
  }

  private Object getClient(LlmSystem system) {
    switch (system) {
      case OPENAI -> {
        return openAiChatClient;
      }
      case MISTRAL -> {
        return mistralAiChatClient;
      }
      case OLLAMA -> {
        return ollamaChatClient;
      }
      case ANSTHROPIC -> {
        return anthropicChatClient;
      }
      default -> throw new IllegalStateException("Unexpected system: " + system);
    }
  }

  private MistralAiChatOptions getMistralOptions(AssistantDto assistantDto) {
    return MistralAiChatOptions.builder()
        .withModel(assistantDto.model())
        .withTopP(
            Float.valueOf(assistantDto.properties().get(AssistantProperties.MODEL_TOP_P.getKey())))
        .withTemperature(
            Float.valueOf(
                assistantDto.properties().get(AssistantProperties.MODEL_TEMPERATURE.getKey())))
        .build();
  }

  private OpenAiChatOptions getOpenAiOptions(AssistantDto assistantDto) {
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

}
