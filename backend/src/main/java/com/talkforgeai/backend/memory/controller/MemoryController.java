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

import com.talkforgeai.backend.memory.service.MemoryService;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MemoryController {

  private final MemoryService memoryService;

  public MemoryController(MemoryService memoryService) {
    this.memoryService = memoryService;
  }

  @PostMapping("/store")
  public void storeInMemory(@RequestBody String info) {
    memoryService.store(info);
  }

  @PostMapping("/search")
  public List<String> retrieve(@RequestBody String search) {
    return memoryService.search(search);
  }

}
