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

package com.talkforgeai.backend.memory;

import com.talkforgeai.backend.assistant.repository.AssistantRepository;
import com.talkforgeai.backend.memory.repository.MemoryRepository;
import com.talkforgeai.backend.memory.service.DBVectorStore;
import jakarta.persistence.EntityManager;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfiguration {

  private final EntityManager entityManager;
  private final OpenAiEmbeddingClient embeddingClient;
  private final MemoryRepository memoryRepository;
  private final AssistantRepository assistantRepository;

  public VectorStoreConfiguration(EntityManager entityManager,
      OpenAiEmbeddingClient embeddingClient,
      MemoryRepository memoryRepository, AssistantRepository assistantRepository) {
    this.entityManager = entityManager;
    this.embeddingClient = embeddingClient;
    this.memoryRepository = memoryRepository;
    this.assistantRepository = assistantRepository;
  }

  @Bean
  VectorStore dbVectorStore() {
    return new DBVectorStore(entityManager, memoryRepository, assistantRepository, embeddingClient);
  }

}
