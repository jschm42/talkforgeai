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

package com.talkforgeai.backend.memory;

import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfiguration {


  private final OpenAiEmbeddingClient embeddingClient;

  public VectorStoreConfiguration(OpenAiEmbeddingClient embeddingClient) {
    this.embeddingClient = embeddingClient;
  }

  @Bean
  SimpleVectorStore simpleVectorStore() {
    SimpleVectorStore simpleVectorStore = new SimpleVectorStore(embeddingClient);

    return simpleVectorStore;
  }

}
