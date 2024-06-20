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

package com.talkforgeai.backend.memory.functions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.talkforgeai.backend.memory.dto.DocumentWithoutEmbeddings;
import com.talkforgeai.backend.memory.dto.MemoryStoreRequestDto;
import com.talkforgeai.backend.memory.service.MemoryService;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryContextStorageFunction implements
    Function<MemoryContextStorageFunction.Request, MemoryContextStorageFunction.Response> {

  public static final String NAME = "memoryContextStorageFunction";
  public static final Logger LOGGER = LoggerFactory.getLogger(MemoryContextStorageFunction.class);

  private final MemoryService memoryService;
  private final MemoryFunctionContext memoryFunctionContext;

  public MemoryContextStorageFunction(MemoryService memoryService,
      MemoryFunctionContext memoryFunctionContext) {
    this.memoryService = memoryService;
    this.memoryFunctionContext = memoryFunctionContext;
  }

  @Override
  public Response apply(Request request) {
    LOGGER.info("Storing information in memory: {}", request.contextInfo());
    MemoryStoreRequestDto requestDto = new MemoryStoreRequestDto(request.contextInfo(),
        memoryFunctionContext.assistantId());
    DocumentWithoutEmbeddings storedDocument = memoryService.store(requestDto.content(),
        requestDto.assistantId());
    return new Response(
        storedDocument.id(),
        "I stored the following information in memory: " + request.contextInfo());
  }

  /**
   * Weather Function request.
   */
  @JsonInclude(Include.NON_NULL)
  public record Request(
      @JsonProperty(required = true, value = "contextInfo") @JsonPropertyDescription("Relevant context information.") String contextInfo) {

  }

  public record Response(String id, String data) {

  }
}
