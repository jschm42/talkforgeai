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
import com.talkforgeai.backend.assistant.domain.MessageEntity;
import com.talkforgeai.backend.assistant.domain.ThreadEntity;
import com.talkforgeai.backend.assistant.dto.AssistantDto;
import com.talkforgeai.backend.assistant.dto.MessageDto;
import com.talkforgeai.backend.assistant.dto.ThreadDto;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AssistantMapper {

  AssistantDto toDto(AssistantEntity assistantEntity) {
    return new AssistantDto(
        assistantEntity.getId(),
        assistantEntity.getCreatedAt(),
        assistantEntity.getName(),
        assistantEntity.getDescription(),
        assistantEntity.getSystem(),
        assistantEntity.getModel(),
        assistantEntity.getInstructions(),
        assistantEntity.getImagePath(),
        mapAssistantProperties(assistantEntity.getProperties())
    );
  }

  AssistantEntity toEntity(AssistantDto assistantDto) {
    AssistantEntity entity = new AssistantEntity();
    entity.setId(assistantDto.id());
    entity.setCreatedAt(assistantDto.createdAt());
    entity.setName(assistantDto.name());
    entity.setDescription(assistantDto.description());
    entity.setModel(assistantDto.model());
    entity.setInstructions(assistantDto.instructions());
    entity.setImagePath(assistantDto.imagePath());
    entity.setSystem(assistantDto.system());
    entity.setProperties(mapProperties(assistantDto.properties()));
    return entity;
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


  public ThreadDto toDto(ThreadEntity threadEntity) {
    return new ThreadDto(threadEntity.getId(), threadEntity.getTitle(),
        threadEntity.getCreatedAt());
  }

  public ThreadEntity toEntity(ThreadDto threadDto) {
    ThreadEntity entity = new ThreadEntity();
    entity.setId(threadDto.id());
    entity.setTitle(threadDto.title());
    entity.setCreatedAt(threadDto.createdAt());
    return entity;
  }

  public MessageDto toDto(MessageEntity messageEntity) {
    return new MessageDto(messageEntity.getId(), messageEntity.getRawContent(),
        messageEntity.getParsedContent(), messageEntity.getCreatedAt(),
        messageEntity.getAssistant().getId(), messageEntity.getThread().getId());
  }

  public MessageEntity toEntity(MessageDto messageDto) {
    MessageEntity entity = new MessageEntity();
    entity.setId(messageDto.id());
    entity.setRawContent(messageDto.rawContent());
    entity.setParsedContent(messageDto.parsedContent());
    return entity;
  }
}
