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

import com.talkforgeai.backend.memory.service.FileVectorStore.DocumentWithoutEmbeddings;
import com.talkforgeai.backend.storage.FileStorageService;
import java.io.File;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class MemoryService {

  public static final Logger LOGGER = LoggerFactory.getLogger(MemoryService.class);

  private final FileStorageService fileStorageService;
  private final VectorStore vectorStore;

  MemoryService(FileStorageService fileStorageService, VectorStore vectorStore) {
    this.fileStorageService = fileStorageService;
    this.vectorStore = vectorStore;
  }

//  @PostConstruct
//  public void init() {
//    if (getFile().exists()) {
//      LOGGER.info("Loading vector file: {}", getFile());
//      vectorStore.load(getFile());
//    }
//  }

  public DocumentWithoutEmbeddings store(String data) {
    Document document = new Document(data);

    LOGGER.info("Adding document: {}", document);
    vectorStore.add(List.of(document));

//    LOGGER.info("Saving vector file: {}", getFile());
//    vectorStore.(getFile());

    return new DocumentWithoutEmbeddings(document.getId(), document.getContent());
  }

  private @NotNull File getFile() {
    return fileStorageService.getTempDirectory().resolve("vectorstore.json").toFile();
  }

  public List<DocumentWithoutEmbeddings> search(SearchRequest searchRequest) {
    LOGGER.info("Searching for: {}", searchRequest);
    return vectorStore.similaritySearch(searchRequest).stream().map(d -> {
      return new DocumentWithoutEmbeddings(d.getId(), d.getContent());
    }).toList();
  }

  public List<DocumentWithoutEmbeddings> list(int page, int pageSize) {
    LOGGER.info("Listing all documents");

    if (vectorStore instanceof ListableVectoreStore listableVectoreStore) {
      return listableVectoreStore.list(page, pageSize).stream()
          .map(d -> new DocumentWithoutEmbeddings(d.getId(), d.getContent())).toList();
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
}
