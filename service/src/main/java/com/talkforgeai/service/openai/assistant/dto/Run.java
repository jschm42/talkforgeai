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

package com.talkforgeai.service.openai.assistant.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Run(
    String id,
    String object,
    @JsonProperty("created_at")
    long createdAt,
    @JsonProperty("assistant_id")
    String assistantId,
    @JsonProperty("thread_id")
    String threadId,
    String status,
    @JsonProperty("started_at")
    Date startedAt,
    @JsonProperty("expires_at")
    Date expiresAt,
    @JsonProperty("cancelled_at")
    Date cancelledAt,
    @JsonProperty("failed_at")
    Date failedAt,
    @JsonProperty("completed_at")
    Date completedAt,
    @JsonProperty("last_error")
    LastError lastError,
    String model,
    String instructions,
    List<ToolRecord> tools,
    @JsonProperty("file_ids")
    List<String> fileIds,
    Map<String, Object> metadata
) {

  public record LastError(String code, String message) {

  }


  public record ToolRecord(
      String type
  ) {

  }

}
