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

import java.util.List;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.SimpleVectorStore;

public class FileVectorStore extends SimpleVectorStore {

  public FileVectorStore(EmbeddingClient embeddingClient) {
    super(embeddingClient);
  }

  List<DocumentWithoutEmbeddings> list() {
    // Convert store1 to a list of DocumentWithoutEmbeddings
    return this.store.values().stream()
        .map(document -> new DocumentWithoutEmbeddings(document.getId(), document.getContent()))
        .toList();
  }

  public record DocumentWithoutEmbeddings(String id, String content) {

  }
}
