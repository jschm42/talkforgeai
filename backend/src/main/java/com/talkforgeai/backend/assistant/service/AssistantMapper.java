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

package com.talkforgeai.backend.assistant.service;


import static java.util.Objects.requireNonNullElse;

import com.talkforgeai.backend.assistant.domain.AssistantEntity;
import com.talkforgeai.backend.assistant.domain.AssistantPropertyValue;
import com.talkforgeai.backend.assistant.dto.AssistantDto;
import com.theokanning.openai.assistants.Assistant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AssistantMapper {


  AssistantDto mapAssistantDto(Assistant assistant, AssistantEntity assistantEntity) {
    return new AssistantDto(
        assistant.getId(),
        assistant.getObject(),
        assistant.getCreatedAt(),
        assistant.getName(),
        assistant.getDescription(),
        assistant.getModel(),
        assistant.getInstructions(),
        assistant.getTools(),
        assistant.getFileIds(),
        assistant.getMetadata(),
        assistantEntity.getImagePath(),
        mapAssistantProperties(assistantEntity.getProperties())
    );
  }

  Assistant mapAssistant(AssistantDto dto) {
    return new Assistant(
        dto.id(),
        dto.object(),
        dto.createdAt(),
        dto.name(),
        dto.description(),
        dto.model(),
        dto.instructions(),
        dto.tools(),
        dto.fileIds(),
        dto.metadata()
    );
  }

  public Map<String, String> mapAssistantProperties(
      Map<String, AssistantPropertyValue> properties) {
    Map<String, String> mappedProperties = new HashMap<>();

    Arrays.stream(AssistantProperties.values()).forEach(property -> {
      AssistantPropertyValue propertyValue = properties.get(property.getKey());
      if (propertyValue != null) {
        mappedProperties.put(
            property.getKey(),
            requireNonNullElse(propertyValue.getPropertyValue(), property.getKey())
        );
      }
    });

    return mappedProperties;
  }

  public Map<String, AssistantPropertyValue> mapProperties(Map<String, String> properties) {
    Map<String, AssistantPropertyValue> mappedProperties = new HashMap<>();

    Arrays.stream(AssistantProperties.values()).forEach(property -> {
      String propertyValue = properties.get(property.getKey());
      AssistantPropertyValue assistantPropertyValue = new AssistantPropertyValue();
      assistantPropertyValue.setPropertyValue(propertyValue);
      mappedProperties.put(property.getKey(), assistantPropertyValue);
    });

    return mappedProperties;
  }
}
