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
import com.talkforgeai.backend.memory.domain.MemoryDocument;
import com.talkforgeai.backend.memory.dto.MemoryListRequestDto;
import com.talkforgeai.backend.memory.dto.MemoryListRequestDto.MemoryListOrderDto;
import com.talkforgeai.backend.memory.dto.MetadataKey;
import com.talkforgeai.backend.memory.repository.MemoryRepository;
import com.talkforgeai.backend.util.UniqueIdUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore.EmbeddingMath;
import org.springframework.ai.vectorstore.filter.Filter.ExpressionType;
import org.springframework.ai.vectorstore.filter.Filter.Key;
import org.springframework.ai.vectorstore.filter.Filter.Value;
import org.springframework.data.domain.PageRequest;

public class DBVectorStore implements ListableVectoreStore {

  public static final Logger LOGGER = LoggerFactory.getLogger(DBVectorStore.class);
  public static final String SEARCH_ASSISTANT_ID = "assistantId";
  public static final String SEARCH_ASSISTANT_NAME = "assistantName";
  public static final String SEARCH_SYSTEM = "system";
  public static final String SEARCH_CONTENT = "content";
  public static final String SEARCH_MESSAGE_TYPE = "messageType";
  private final EntityManager entityManager;
  private final MemoryRepository memoryRepository;
  private final AssistantRepository assistantRepository;
  private final EmbeddingModel embeddingModel;

  public DBVectorStore(EntityManager entityManager, MemoryRepository memoryRepository,
      AssistantRepository assistantRepository,
      EmbeddingModel embeddingModel) {
    this.entityManager = entityManager;
    this.memoryRepository = memoryRepository;
    this.assistantRepository = assistantRepository;
    this.embeddingModel = embeddingModel;
  }

  @Transactional
  @Override
  public void add(List<Document> documents) {
    // Convert documents to MemoryDocument
    List<MemoryDocument> memoryDocuments = documents.stream()
        .filter(document -> {
          int countDocs;
          if (document.getMetadata().get(MetadataKey.CONVERSATION_ID.key()) == null) {
            countDocs = memoryRepository.countByContentAndEmptyAssistant(document.getContent());
          } else {
            countDocs = memoryRepository.countByContentAndAssistantId(document.getContent(),
                (String) document.getMetadata().get(MetadataKey.CONVERSATION_ID.key()));
          }

          if (countDocs > 0) {
            LOGGER.warn(
                "Document with content '{}' already exists in the database. Document will be ignored.",
                document.getContent());
          }
          return countDocs == 0;
        })
        .map(document -> {
          MemoryDocument documentEntity = new MemoryDocument();

          LOGGER.info("Calling EmbeddingClient for document id = {}", document.getId());
          List<Double> embedding = this.embeddingModel.embed(document);
          documentEntity.setEmbeddings(
              embedding.stream().mapToDouble(Double::doubleValue).toArray());
          documentEntity.setId(UniqueIdUtil.generateMemoryId());
          documentEntity.setContent(document.getContent());
          documentEntity.setCreatedAt(new Date());

          documentEntity.setSystem(LlmSystem.OPENAI.name());
          documentEntity.setModel(OpenAiApi.DEFAULT_EMBEDDING_MODEL);
          documentEntity.setRunId((String) document.getMetadata().get(MetadataKey.RUN_ID.key()));
          documentEntity.setMessageType(
              (String) document.getMetadata().get(MetadataKey.MESSAGE_TYPE.key()));
          if (document.getMetadata().containsKey(MetadataKey.CONVERSATION_ID.key())) {
            assistantRepository.findById(
                    (String) document.getMetadata().get(MetadataKey.CONVERSATION_ID.key()))
                .ifPresent(documentEntity::setAssistant);
          }

          return documentEntity;
        })
        .toList();

    memoryRepository.saveAll(memoryDocuments);
  }

  @Transactional
  @Override
  public Optional<Boolean> delete(List<String> idList) {
    memoryRepository.deleteAllById(idList);
    return Optional.of(true);
  }

  @Override
  public List<Document> similaritySearch(SearchRequest request) {
    LOGGER.info("Similarity search request: {}", request);

    String assistantId = null;
    if (request.getFilterExpression() != null) {
      ExpressionType type = request.getFilterExpression().type();
      if (type == ExpressionType.EQ) {
        Key left = (Key) request.getFilterExpression().left();
        Value right = (Value) request.getFilterExpression().right();

        if (left.key().equals(SEARCH_ASSISTANT_ID)) {
          assistantId = right.value().toString();
        }
      }
    }

    List<Double> userQueryEmbedding = getUserQueryEmbedding(request.getQuery());

    List<MemoryDocument> documents;
    if (assistantId == null) {
      LOGGER.info("Searching for all documents");
      documents = memoryRepository.findAll();
    } else {
      LOGGER.info("Searching for documents with assistantId = {}", assistantId);
      documents = memoryRepository.findAllByAssistantId(assistantId);
    }

    // Convert documents to Map<String, MemoryDocument>
    Map<String, MemoryDocument> documentMap = documents.stream()
        .collect(Collectors.toMap(MemoryDocument::getId, document -> document));

    return documents
        .stream()
        .map(entry -> {
          double[] embeddings = entry.getEmbeddings();

          List<Double> doubleEmbeddings = new ArrayList<>();

          for (double b : embeddings) {
            doubleEmbeddings.add(b);
          }

          return new Similarity(entry.getId(),
              EmbeddingMath.cosineSimilarity(userQueryEmbedding, doubleEmbeddings));
        })
        .filter(s -> s.score() >= request.getSimilarityThreshold())
        .sorted(Comparator.<Similarity>comparingDouble(Similarity::score).reversed())
        .limit(request.getTopK())
        .map(s -> {
          MemoryDocument memoryDocument = documentMap.get(s.key());

          Map<String, Object> metadata = new HashMap<>();
          metadata.put(MetadataKey.SYSTEM.key(), memoryDocument.getSystem());
          metadata.put(MetadataKey.MODEL.key(), memoryDocument.getModel());
          metadata.put(MetadataKey.CONVERSATION_ID.key(),
              memoryDocument.getAssistant() == null ? null : memoryDocument.getAssistant().getId());
          metadata.put(MetadataKey.ASSISTANT_NAME.key(),
              memoryDocument.getAssistant() == null ? null
                  : memoryDocument.getAssistant().getName());
          metadata.put(MetadataKey.MESSAGE_TYPE.key(), memoryDocument.getMessageType());

          return new Document(s.key(), memoryDocument.getContent(), metadata);
        })
        .peek(document -> LOGGER.info("Similarity search result: {}", document))
        .toList();
  }

  private List<Double> getUserQueryEmbedding(String query) {
    return this.embeddingModel.embed(query);
  }

  @Override
  public int count() {
    return (int) memoryRepository.count();
  }

  @Override
  public List<Document> list() {
    return memoryRepository.findAll().stream()
        .map(this::mapDocument)
        .toList();
  }

  @Override
  public List<Document> list(MemoryListRequestDto listRequest) {
    PageRequest pageRequest = PageRequest.of(listRequest.page() - 1,
        listRequest.pageSize() == -1 ? Integer.MAX_VALUE : listRequest.pageSize());

    Map<String, String> searchMap = listRequest.search();
    StringBuilder query = new StringBuilder(
        "SELECT md FROM MemoryDocument md LEFT JOIN AssistantEntity a ON md.assistant.id = a.id "
            + "WHERE 1 = 1 ");

    if (searchMap != null && !searchMap.isEmpty()) {
      appendQueryCondition(query, searchMap, SEARCH_CONTENT, "md.content LIKE :content");
      appendQueryCondition(query, searchMap, SEARCH_ASSISTANT_ID,
          "a.id LIKE :assistantId");
      appendQueryCondition(query, searchMap, SEARCH_ASSISTANT_NAME,
          "a.name LIKE :assistantName");
      appendQueryCondition(query, searchMap, SEARCH_SYSTEM,
          "md.system = :system");
      appendQueryCondition(query, searchMap, SEARCH_MESSAGE_TYPE,
          "md.messageType = :messageType");
    }

    List<MemoryListOrderDto> sortBy = listRequest.sortBy();

    // Create order by clause
    if (sortBy != null && !sortBy.isEmpty()) {
      query.append(" ORDER BY ");
      for (MemoryListOrderDto order : sortBy) {
        if (order.key().equals(SEARCH_ASSISTANT_ID)) {
          query.append("a.id").append(" ").append(order.order()).append(", ");
        } else if (order.key().equals(SEARCH_ASSISTANT_NAME)) {
          query.append("a.name").append(" ").append(order.order()).append(", ");
        } else {
          query.append("md.").append(order.key()).append(" ").append(order.order()).append(", ");
        }
        query.delete(query.length() - 2, query.length());
      }
    }

    TypedQuery<MemoryDocument> typedQuery = entityManager.createQuery(query.toString(),
            MemoryDocument.class)
        .setFirstResult(pageRequest.getPageNumber() * pageRequest.getPageSize())
        .setMaxResults(pageRequest.getPageSize());

    if (searchMap != null && !searchMap.isEmpty()) {
      setQueryParameter(typedQuery, searchMap, SEARCH_CONTENT,
          "%" + searchMap.get(SEARCH_CONTENT) + "%");
      setQueryParameter(typedQuery, searchMap, SEARCH_ASSISTANT_ID,
          "%" + searchMap.get(SEARCH_ASSISTANT_ID) + "%");
      setQueryParameter(typedQuery, searchMap, SEARCH_ASSISTANT_NAME,
          "%" + searchMap.get(SEARCH_ASSISTANT_NAME) + "%");
      setQueryParameter(typedQuery, searchMap, SEARCH_SYSTEM, searchMap.get(SEARCH_SYSTEM));
      setQueryParameter(typedQuery, searchMap, SEARCH_MESSAGE_TYPE,
          searchMap.get(SEARCH_MESSAGE_TYPE));
    }

    return typedQuery.getResultList().stream()
        .map(this::mapDocument)
        .toList();
  }

  @Transactional
  @Override
  public void deleteAll() {
    memoryRepository.deleteAll();
  }

  private Document mapDocument(MemoryDocument memoryDocument) {
    Map<String, Object> metadata = new HashMap<>();

    metadata.put(MetadataKey.SYSTEM.key(), memoryDocument.getSystem());
    metadata.put(MetadataKey.MODEL.key(), memoryDocument.getModel());
    metadata.put(MetadataKey.CONVERSATION_ID.key(),
        memoryDocument.getAssistant() == null ? null : memoryDocument.getAssistant().getId());
    metadata.put(MetadataKey.ASSISTANT_NAME.key(),
        memoryDocument.getAssistant() == null ? null : memoryDocument.getAssistant().getName());
    metadata.put(MetadataKey.MESSAGE_TYPE.key(), memoryDocument.getMessageType());

    return new Document(
        memoryDocument.getId(),
        memoryDocument.getContent(),
        metadata);
  }

  private void appendQueryCondition(StringBuilder query, Map<String, String> searchMap, String key,
      String condition) {
    if (searchMap.get(key) != null && !searchMap.get(key).isBlank()) {
      query.append(" AND ").append(condition);
    }
  }

  private void setQueryParameter(TypedQuery<MemoryDocument> query, Map<String, String> searchMap,
      String key, String value) {
    if (searchMap.get(key) != null && !searchMap.get(key).isBlank()) {
      query.setParameter(key, value);
    }
  }

  public record Similarity(String key, double score) {

  }

}
