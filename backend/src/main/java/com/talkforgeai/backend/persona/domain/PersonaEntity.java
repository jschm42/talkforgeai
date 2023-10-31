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

package com.talkforgeai.backend.persona.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.util.*;

@Entity
@Table(name = "PERSONA")
public class PersonaEntity {
    @CreationTimestamp
    Date createdAt;

    @UpdateTimestamp
    Date modifiedAt;

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(unique = true, length = 32, nullable = false)
    private String name;

    @Column(length = 256, nullable = false)
    private String description;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String background;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String personality;

    @Enumerated(EnumType.STRING)
    private List<RequestFunction> requestFunctions = new ArrayList<>();

    @Column(length = 128)
    private String imagePath;

    @ElementCollection
    @MapKeyColumn(name = "property_key")
    @CollectionTable(name = "persona_properties", joinColumns = @JoinColumn(name = "persona_id"))
    @Column(name = "property_value")
    private Map<String, PersonaPropertyValue> properties = new HashMap<>();

    public List<RequestFunction> getRequestFunctions() {
        return requestFunctions;
    }

    public void setRequestFunctions(List<RequestFunction> requestFunctions) {
        this.requestFunctions = requestFunctions;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public Map<String, PersonaPropertyValue> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, PersonaPropertyValue> properties) {
        this.properties = properties;
    }
}
