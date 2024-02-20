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

package com.talkforgeai.service;

import static com.theokanning.openai.service.OpenAiService.defaultClient;
import static com.theokanning.openai.service.OpenAiService.defaultObjectMapper;
import static com.theokanning.openai.service.OpenAiService.defaultRetrofit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.service.properties.OpenAIProperties;
import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.service.OpenAiService;
import java.time.Duration;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;

@Configuration
public class OpenAIConfiguration {

  public static final int TIMEOUT_MS = 1000 * 60 * 5;

  private OpenAIProperties properties;

  public OpenAIConfiguration(OpenAIProperties properties) {
    this.properties = properties;
  }

  @Bean
  OpenAiService openAiService() {
    ObjectMapper mapper = defaultObjectMapper();
    OkHttpClient client = defaultClient(properties.apiKey(), Duration.ofMinutes(5))
        .newBuilder()
        .addInterceptor(new HttpLoggingInterceptor().setLevel(Level.BODY))
        .build();

    Retrofit retrofit = defaultRetrofit(client, mapper);
    OpenAiApi api = retrofit.create(OpenAiApi.class);
    return new OpenAiService(api);
  }

}
