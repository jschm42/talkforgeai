/*
 * Copyright (c) 2023 Jean Schmitz.
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

package com.talkforgeai.backend.assistant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.talkforgeai.service.openai.assistant.dto.Tool;

import java.util.List;
import java.util.Map;

public record AssistantDto(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String id,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        String object,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("created_at")
        String createdAt,
        String name,
        String description,
        String model,
        String instructions,
        List<Tool> tools,
        @JsonProperty("file_ids")
        List<String> fileIds,
        Map<String, Object> metadata,
        @JsonProperty("image_path")
        String imagePath,
        Map<String, String> properties) {
}
