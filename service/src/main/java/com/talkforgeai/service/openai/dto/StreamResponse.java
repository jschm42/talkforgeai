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

package com.talkforgeai.service.openai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record StreamResponse(String id,
                             String object,
                             StreamResponseDelta delta) {

  public record StreamResponseDelta(List<StreamResponseContent> content) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StreamResponseContent(Integer index,
                                        String type,
                                        ContentText text,
                                        @JsonProperty("image_file")
                                        ContentImage imageFile
    ) {

      public record ContentText(String value,
                                List<String> annotations) {

      }

      public record ContentImage(@JsonProperty("file_id") String fileId) {

      }

    }

  }

  public record ResponseUsage(@JsonProperty("prompt_tokens") int promptTokens,
                              @JsonProperty("completion_tokens") int completionTokens,
                              @JsonProperty("total_tokens") int totalTokens) {

  }
}

