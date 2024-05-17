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

package com.talkforgeai.backend.memory.domain;

import com.talkforgeai.backend.assistant.domain.AssistantEntity;
import com.talkforgeai.backend.memory.exceptions.MemoryException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Date;

@Entity
@Table(name = "memory_document")
public class MemoryDocument {

  @Id
  private String id;

  @Column(name = "created_at")
  private Date createdAt;

  @Lob
  @Column(name = "content")
  private String content;

  @Lob
  @Column(name = "embeddings")
  private String embeddings;

  @Column(name = "system")
  private String system;

  @Column(name = "model")
  private String model;

  @ManyToOne(optional = true)
  private AssistantEntity assistant;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  // Convert byte[] back to double[]
  public double[] getEmbeddings() {
    if (embeddings == null) {
      return null;
    }
    byte[] bytes = Base64.getDecoder().decode(embeddings);
    try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(bis)) {
      return (double[]) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new MemoryException("Error reading embedding from byte array", e);
    }
  }

  // Convert double[] to byte[] for storage
  public void setEmbeddings(double[] embeddingArray) {
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos)) {
      out.writeObject(embeddingArray);
      this.embeddings = Base64.getEncoder().encodeToString(bos.toByteArray());
    } catch (IOException e) {
      throw new MemoryException("Error writing embedding to byte array", e);
    }
  }

  public void setEmbeddings(String embeddings) {
    this.embeddings = embeddings;
  }

  public AssistantEntity getAssistant() {
    return assistant;
  }

  public void setAssistant(AssistantEntity assistant) {
    this.assistant = assistant;
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
}
