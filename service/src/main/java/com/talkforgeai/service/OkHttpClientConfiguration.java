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

package com.talkforgeai.service;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Configuration
public class OkHttpClientConfiguration {
    public static final int TIMEOUT_MS = 1000 * 60 * 5;

    @Bean
    public OkHttpClient createOkHttpClient() {
        return new OkHttpClient.Builder()
                .callTimeout(TIMEOUT_MS, MILLISECONDS)
                .readTimeout(TIMEOUT_MS, MILLISECONDS)
                .writeTimeout(TIMEOUT_MS, MILLISECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
    }
}
