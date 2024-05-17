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

package com.talkforgeai.backend.memory.controller;

import com.talkforgeai.backend.memory.dto.DocumentWithoutEmbeddings;
import com.talkforgeai.backend.memory.dto.MemoryListRequestDto;
import com.talkforgeai.backend.memory.dto.MemoryRemoveRequestDto;
import com.talkforgeai.backend.memory.dto.MemorySearchRequestDto;
import com.talkforgeai.backend.memory.dto.MemoryStoreRequestDto;
import com.talkforgeai.backend.memory.service.MemoryService;
import java.util.List;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/memory")
public class MemoryController {

  private final MemoryService memoryService;

  public MemoryController(MemoryService memoryService) {
    this.memoryService = memoryService;
  }

  @PostMapping("/store")
  public DocumentWithoutEmbeddings storeInMemory(@RequestBody MemoryStoreRequestDto request) {
    return memoryService.store(request);
  }

  @PostMapping("/search")
  public List<DocumentWithoutEmbeddings> search(@RequestBody MemorySearchRequestDto search) {
    return memoryService.search(SearchRequest.query(search.query())
        .withSimilarityThreshold(search.similarityThreshold())
        .withTopK(search.topK()));
  }

  @PostMapping("/list")
  public List<DocumentWithoutEmbeddings> list(@RequestBody MemoryListRequestDto request) {
    return memoryService.list(request);
  }

  @PostMapping("/remove")
  public void remove(@RequestBody MemoryRemoveRequestDto request) {
    memoryService.remove(request.memoryIds());
  }

  @PostMapping("/clear")
  public void clear() {
    memoryService.clear();
  }

  @GetMapping("/count")
  public int count() {
    return memoryService.count();
  }

  @GetMapping("/supports/list")
  public boolean supportsList() {
    return memoryService.supportsList();
  }
}
