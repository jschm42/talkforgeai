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

package com.talkforgeai.backend.memory.service;

import com.talkforgeai.backend.assistant.dto.LlmSystem;
import com.talkforgeai.backend.assistant.repository.AssistantRepository;
import com.talkforgeai.backend.memory.dto.DocumentWithoutEmbeddings;
import com.talkforgeai.backend.memory.dto.MemoryListRequestDto;
import com.talkforgeai.backend.memory.dto.MetadataKey;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.autoconfigure.openai.OpenAiEmbeddingProperties;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class MemoryService {

  public static final Logger LOGGER = LoggerFactory.getLogger(MemoryService.class);

  private final AssistantRepository assistantRepository;
  private final VectorStore vectorStore;

  MemoryService(AssistantRepository assistantRepository, VectorStore vectorStore) {
    this.assistantRepository = assistantRepository;
    this.vectorStore = vectorStore;
  }

  public DocumentWithoutEmbeddings store(String content, String assistantId) {
    return this.store(content, assistantId, "", "");
  }

  public DocumentWithoutEmbeddings store(String content, String assistantId, String runId,
      String messageType) {
    Document document = new Document(content);

    document.getMetadata().put(MetadataKey.SYSTEM.key(), LlmSystem.OPENAI.name());
    document.getMetadata()
        .put(MetadataKey.MODEL.key(), OpenAiEmbeddingProperties.DEFAULT_EMBEDDING_MODEL);
    document.getMetadata().put(MetadataKey.RUN_ID.key(), runId);
    document.getMetadata().put(MetadataKey.MESSAGE_TYPE.key(), messageType);

    if (assistantId != null && !assistantId.isBlank()) {
      document.getMetadata().put(MetadataKey.CONVERSATION_ID.key(), assistantId);
      var assistant = assistantRepository.findById(assistantId);
      assistant.ifPresent(
          a -> document.getMetadata().put(MetadataKey.ASSISTANT_NAME.key(), a.getName()));
    }

    LOGGER.info("Adding document: {}", document);
    vectorStore.add(List.of(document));

    return DocumentWithoutEmbeddings.from(document);
  }

  public List<DocumentWithoutEmbeddings> search(SearchRequest searchRequest) {
    LOGGER.info("Searching for: {}", searchRequest);
    return vectorStore.similaritySearch(searchRequest).stream()
        .map(DocumentWithoutEmbeddings::from).toList();
  }

  public List<DocumentWithoutEmbeddings> list(MemoryListRequestDto request) {
    LOGGER.info("Listing documents: {}", request);

    if (vectorStore instanceof ListableVectoreStore listableVectoreStore) {
      return listableVectoreStore.list(request).stream()
          .map(DocumentWithoutEmbeddings::from).toList();
    } else {
      throw new UnsupportedOperationException("VectorStore does not support listing");
    }
  }

  public boolean supportsList() {
    return vectorStore instanceof ListableVectoreStore;
  }

  public int count() {
    if (vectorStore instanceof ListableVectoreStore listableVectoreStore) {
      return listableVectoreStore.count();
    } else {
      throw new UnsupportedOperationException("VectorStore does not support count");
    }
  }

  public void remove(List<String> strings) {
    LOGGER.info("Removing documents: {}", strings);
    vectorStore.delete(strings);
  }

  public void clear() {
    LOGGER.info("Deleting all memory");

    if (vectorStore instanceof ListableVectoreStore listableVectoreStore) {
      listableVectoreStore.deleteAll();
    } else {
      throw new UnsupportedOperationException("VectorStore does not support listing");
    }
  }
}
