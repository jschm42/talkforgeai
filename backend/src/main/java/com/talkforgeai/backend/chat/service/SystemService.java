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

package com.talkforgeai.backend.chat.service;

import com.talkforgeai.backend.chat.exception.ChatException;
import com.talkforgeai.backend.persona.domain.GlobalSystem;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SystemService {
    public static final String DEFAULT_SYSTEM = "";

    public static final String IMAGE_GEN_SYSTEM = "You can generate DALL-E 2 prompts, that will be converted to images. Use the following format: " +
            "Place the content between the following tags: <image-prompt></image-prompt>.";

    public static final String PLANTUML_SYSTEM = "Always mark the plantuml markup with language = \"plantuml\". Generate PlantUML code when asked to generate class, sequence or activity diagrams.";

    public String getContent(GlobalSystem system) {
        Objects.requireNonNull(system, "Global system cannot be null.");

        switch (system) {
            case DEFAULT -> {
                return DEFAULT_SYSTEM;
            }
            case IMAGE_GEN -> {
                return IMAGE_GEN_SYSTEM;
            }
            case PLANTUML -> {
                return PLANTUML_SYSTEM;
            }
        }

        throw new ChatException("Unknown global system: " + system);
    }
}
