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

import com.talkforgeai.backend.memory.domain.MemoryDocument;
import com.talkforgeai.backend.memory.domain.MemoryDocumentMetadataValue;
import com.talkforgeai.backend.memory.dto.MemoryListRequestDto;
import com.talkforgeai.backend.memory.repository.MemoryRepository;
import com.talkforgeai.backend.service.UniqueIdGenerator;
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
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore.EmbeddingMath;
import org.springframework.data.domain.PageRequest;

public class DBVectorStore implements ListableVectoreStore {

  public static final Logger LOGGER = LoggerFactory.getLogger(DBVectorStore.class);

  private final MemoryRepository memoryRepository;
  private final EmbeddingClient embeddingClient;
  private final UniqueIdGenerator uniqueIdGenerator;

  public DBVectorStore(MemoryRepository memoryRepository, EmbeddingClient embeddingClient,
      UniqueIdGenerator uniqueIdGenerator) {
    this.memoryRepository = memoryRepository;
    this.embeddingClient = embeddingClient;
    this.uniqueIdGenerator = uniqueIdGenerator;
  }

  @Override
  public void add(List<Document> documents) {
    // Convert documents to MemoryDocument
    List<MemoryDocument> memoryDocuments = documents.stream()
        .map(document -> {
          MemoryDocument documentEntity = new MemoryDocument();

          LOGGER.info("Calling EmbeddingClient for document id = {}", document.getId());
          List<Double> embedding = this.embeddingClient.embed(document);
          documentEntity.setEmbeddings(
              embedding.stream().mapToDouble(Double::doubleValue).toArray());
          // Convert List<Double> to byte[]
          documentEntity.setId(uniqueIdGenerator.generateMemoryId());
          documentEntity.setContent(document.getContent());
          documentEntity.setCreatedAt(new Date());

          document.getMetadata().forEach((key, value) -> documentEntity.getMetadata()
              .put(key, MemoryDocumentMetadataValue.of(value.toString())));

          return documentEntity;
        })
        .toList();

    memoryRepository.saveAll(memoryDocuments);
  }

  @Override
  public Optional<Boolean> delete(List<String> idList) {
    return Optional.empty();
  }

  @Override
  public List<Document> similaritySearch(SearchRequest request) {
    if (request.getFilterExpression() != null) {
      throw new UnsupportedOperationException(
          "The [" + this.getClass() + "] doesn't support metadata filtering!");
    }

    List<Double> userQueryEmbedding = getUserQueryEmbedding(request.getQuery());

    List<MemoryDocument> documents = memoryRepository.findAll();
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
          return new Document(s.key(), memoryDocument.getContent(), new HashMap<>());
        })
        .toList();
  }

  private List<Double> getUserQueryEmbedding(String query) {
    return this.embeddingClient.embed(query);
  }

  @Override
  public int count() {
    return (int) memoryRepository.count();
  }

  @Override
  public List<Document> list() {
    return memoryRepository.findAll().stream()
        .map(memoryDocument -> new Document(memoryDocument.getId(), memoryDocument.getContent(),
            new HashMap<>()))
        .toList();
  }

  @Override
  public List<Document> list(MemoryListRequestDto listRequest) {

    return memoryRepository.findAll(
            PageRequest.of(listRequest.page() - 1,
                listRequest.pageSize() == -1 ? Integer.MAX_VALUE : listRequest.pageSize())).stream()
        .map(memoryDocument -> new Document(memoryDocument.getId(), memoryDocument.getContent(),
            new HashMap<>()))
        .toList();
  }

  public record Similarity(String key, double score) {

  }
}
