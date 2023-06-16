package com.talkforgeai.talkforgeaiserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.talkforgeaiserver.properties.OpenAIProperties;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.service.OpenAiService;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;

import java.time.Duration;

import static com.theokanning.openai.service.OpenAiService.defaultClient;
import static com.theokanning.openai.service.OpenAiService.defaultRetrofit;

@Configuration
public class OpenAIServiceConfiguration {
    public static final int TIMEOUT_MS = 1000 * 60 * 5;

    private final OpenAIProperties openAIProperties;

    public OpenAIServiceConfiguration(OpenAIProperties openAIProperties) {
        this.openAIProperties = openAIProperties;
    }

    @Bean
    public OpenAiService openAIService() {
        ObjectMapper mapper = OpenAiService.defaultObjectMapper();
        OkHttpClient client = defaultClient(openAIProperties.apiKey(), Duration.ofMillis(TIMEOUT_MS))
                .newBuilder()
                .build();
        Retrofit retrofit = defaultRetrofit(client, mapper);

        OpenAiApi api = retrofit.create(OpenAiApi.class);
        return new OpenAiService(api);
    }
}
