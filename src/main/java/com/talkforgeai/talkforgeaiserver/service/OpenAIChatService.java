package com.talkforgeai.talkforgeaiserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.talkforgeaiserver.properties.OpenAIProperties;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenAIChatService {
    private final OpenAIProperties openAIProperties;
    private OkHttpClient client;
    private ObjectMapper objectMapper;
    private String apiKey;

    public OpenAIChatService(OpenAIProperties openAIProperties) {
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.openAIProperties = openAIProperties;
    }

    public ChatGptResponse submit(List<ChatMessage> messages) {
        OpenAiService service = new OpenAiService("your_token");
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
            .messages(messages)
            .model("gpt-3.5-turbo")
            .build();

        service.createChatCompletion(completionRequest).getChoices().forEach(System.out::println);

        return null;
    }

}
