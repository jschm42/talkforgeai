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
