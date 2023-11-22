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

package com.talkforgeai.backend.assistant.domain;

import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "assistant")
public class AssistantEntity {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "image_path", length = 100)
    private String imagePath;

    @ElementCollection
    @MapKeyColumn(name = "property_key")
    @CollectionTable(name = "assistant_properties", joinColumns = @JoinColumn(name = "assistant_id"))
    @Column(name = "property_value")
    private Map<String, AssistantPropertyValue> properties = new HashMap<>();

    // Standard getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, AssistantPropertyValue> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, AssistantPropertyValue> properties) {
        this.properties = properties;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}

