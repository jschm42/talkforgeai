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

package com.talkforgeai.backend.persona.dto;

import com.talkforgeai.backend.persona.domain.RequestFunction;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record PersonaDto(UUID personaId,
                         String name,
                         String description,
                         String background,
                         String personality,

                         List<RequestFunction> requestFunctions,
                         String imageUrl,
                         String imagePath,
                         Map<String, String> properties) {

    @Override
    public String toString() {
        return "PersonaDto{" +
                "personaId=" + personaId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", background='" + background + '\'' +
                ", personality='" + personality + '\'' +
                ", requestFunctions=" + requestFunctions +
                ", imageUrl='" + imageUrl + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", properties=" + properties +
                '}';
    }
}
