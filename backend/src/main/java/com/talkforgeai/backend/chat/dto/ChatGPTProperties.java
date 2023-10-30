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

package com.talkforgeai.backend.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChatGPTProperties(
        @JsonProperty("max_tokens")
        int maxTokens,
        double temperature,

        @JsonProperty("top_p")
        float topP,

        @JsonProperty("frequency_penalty")
        float frequencyPenalty,

        @JsonProperty("presence_penalty")
        float presencePenalty) {
    public ChatGPTProperties() {
        this(2048, 0.7f, 1.0f, 0.0f, 0.0f);
    }

}
