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

package com.talkforgeai.backend.memory.dto;


import java.util.Map;
import org.springframework.ai.document.Document;

public record DocumentWithoutEmbeddings(String id, String content, String assistantId,
                                        String assistantName,
                                        String system, String model) {

  private static String getMetadataValue(Map<String, Object> meta, MetadataKey key) {
    return (String) meta.getOrDefault(key.key(), null);
  }

  public static DocumentWithoutEmbeddings from(Document document) {
    Map<String, Object> meta = document.getMetadata();
    return new DocumentWithoutEmbeddings(
        document.getId(),
        document.getContent(),
        getMetadataValue(meta, MetadataKey.ASSISTANT_ID),
        getMetadataValue(meta, MetadataKey.ASSISTANT_NAME),
        getMetadataValue(meta, MetadataKey.SYSTEM),
        getMetadataValue(meta, MetadataKey.MODEL)
    );
  }
}