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

package com.talkforgeai.backend.memory.repository;

import com.talkforgeai.backend.memory.domain.MemoryDocument;
import com.talkforgeai.backend.memory.domain.MemoryDocumentMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemoryRepository extends JpaRepository<MemoryDocument, String> {

  @NotNull
  Page<MemoryDocument> findAll(@NotNull Pageable pageable);

  @Query("SELECT COUNT(m) FROM MemoryDocument m WHERE m.content = :content AND KEY(m.metadata) = :key AND VALUE(m.metadata) = :value")
  int countByContentAndKeyValue(String content, String key, MemoryDocumentMetadataValue value);

  @Query("SELECT COUNT(m) FROM MemoryDocument m WHERE m.content = :content AND KEY(m.metadata) != :key")
  int countByContentExcludingKey(String content, String key);

}
