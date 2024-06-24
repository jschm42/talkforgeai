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

package com.talkforgeai.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfiguration {

  @Value("${talkforgeai.request.timeout}")
  private int requestTimeout;

  @Value("${talkforgeai.connect.timeout}")
  private int connectTimeout;

  @Value("${spring.ai.openai.api-key}")
  private String openAiApiKey;

  @Value("${spring.ai.openai.base-url}")
  private String openAiBaseUrl;

  @Value("${spring.ai.ollama.base-url}")
  private String ollamaBaseUrl;

  @Value("${elevenlabs.api-key}")
  private String elevenLabsApiKey;

  @Value("${elevenlabs.api-url}")
  private String elevenLabsBaseUrl;

  @Bean(name = "elevenLabsRestClient")
  public RestClient elevenLabsRestClient() {
    return RestClient.builder()
        .baseUrl(elevenLabsBaseUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + elevenLabsApiKey)
        .build();
  }

  @Bean(name = "openAiRestClient")
  public RestClient openAiRestClient() {
    return RestClient.builder()
        .baseUrl(openAiBaseUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAiApiKey)
        .build();
  }

  @Bean(name = "ollamaAiRestClient")
  public RestClient ollamaAiRestClient() {
    return RestClient.builder()
        .baseUrl(ollamaBaseUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder
        .requestFactory(this::clientHttpRequestFactory)
        .build();
  }

  private ClientHttpRequestFactory clientHttpRequestFactory() {
    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
    factory.setConnectionRequestTimeout(connectTimeout);
    factory.setConnectTimeout(requestTimeout);
    return factory;
  }
}
