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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "message")
public class MessageEntity {

  @Id
  @Column(name = "id", length = 50)
  private String id;

  @Lob
  @Column(name = "raw_content")
  private String rawContent;

  @Lob
  @Column(name = "parsed_content")
  private String parsedContent;
  private String role;
  
  @Column(name = "created_at", nullable = false)
  private Date createdAt;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "thread_id", nullable = false)
  private ThreadEntity thread;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "assistant_id", nullable = false)
  private AssistantEntity assistant;

  // Standard getters and setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRawContent() {
    return rawContent;
  }

  public void setRawContent(String rawContent) {
    this.rawContent = rawContent;
  }

  public String getParsedContent() {
    return parsedContent;
  }

  public void setParsedContent(String parsedContent) {
    this.parsedContent = parsedContent;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public ThreadEntity getThread() {
    return thread;
  }

  public void setThread(ThreadEntity thread) {
    this.thread = thread;
  }

  public AssistantEntity getAssistant() {
    return assistant;
  }

  public void setAssistant(AssistantEntity assistant) {
    this.assistant = assistant;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

}
