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

package com.talkforgeai.service.openai.assistant.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record ListMessageResponse(
        String object,
        List<Message> data,
        String firstId,
        String lastId,
        boolean hasMore) {
    public record Message(
            String id,
            String object,
            long createdAt,
            String threadId,
            String role,
            List<ContentItem> content,
            List<String> fileIds,
            Optional<String> assistantId,
            Optional<String> runId,
            Map<String, Object> metadata
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record ContentItem(
                String type,
                TextContent text,
                @JsonProperty("image_file")
                ImageContent imageFile
        ) {
            public record TextContent(
                    String value,
                    List<Annotation> annotations
            ) {
                public record Annotation(
                        // Define the annotation structure here
                ) {
                }
            }

            public record ImageContent(
                    @JsonProperty("file_id")
                    String fileId
            ) {
            }
        }
    }
}
