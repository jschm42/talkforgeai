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

package com.talkforgeai.backend.memory.domain;

import com.talkforgeai.backend.util.UniqueIdUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "memory_metadata")
public class MemoryMetadata {

  @Id
  private String id;
  @ManyToOne
  private MemoryDocument memoryDocument;
  private String metadataKey;
  @Lob
  private String metadataValue;

  public static MemoryMetadata of(MemoryDocument document, String key, String value) {
    MemoryMetadata metadata = new MemoryMetadata();
    metadata.setId(UniqueIdUtil.generateMemoryMetaId());
    metadata.setMemoryDocument(document);
    metadata.setMetadataKey(key);
    metadata.setMetadataValue(value);
    return metadata;
  }

  public String getMetadataKey() {
    return metadataKey;
  }

  public void setMetadataKey(String metadataKey) {
    this.metadataKey = metadataKey;
  }

  public String getMetadataValue() {
    return metadataValue;
  }

  public void setMetadataValue(String value) {
    this.metadataValue = value;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public MemoryDocument getMemoryDocument() {
    return memoryDocument;
  }

  public void setMemoryDocument(MemoryDocument memoryDocument) {
    this.memoryDocument = memoryDocument;
  }


}
