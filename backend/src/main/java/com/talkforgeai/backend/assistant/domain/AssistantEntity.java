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

package com.talkforgeai.backend.assistant.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import java.util.Date;
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
  @Column(name = "name", length = 50)
  private String name;
  @Column(name = "description", length = 200)
  private String description;
  @Column(name = "system", length = 20)
  private String system;
  @Column(name = "model", length = 30)
  private String model;
  @Lob
  @Column(name = "instructions")
  private String instructions;
  @ElementCollection
  @MapKeyColumn(name = "property_key")
  @CollectionTable(name = "assistant_properties", joinColumns = @JoinColumn(name = "assistant_id"))
  @Column(name = "property_value")
  private Map<String, AssistantPropertyValue> properties = new HashMap<>();
  @Column(name = "created_at")
  private Date createdAt;

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

  public String getSystem() {
    return system;
  }

  public void setSystem(String system) {
    this.system = system;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public String getInstructions() {
    return instructions;
  }

  public void setInstructions(String instructions) {
    this.instructions = instructions;
  }

}

