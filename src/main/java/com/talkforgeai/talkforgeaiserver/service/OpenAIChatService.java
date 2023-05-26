package com.talkforgeai.talkforgeaiserver.service;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenAIChatService {

    private final OpenAiService service;

    public OpenAIChatService(OpenAiService service) {
        this.service = service;
    }

    public List<ChatCompletionChoice> submit(List<ChatMessage> messages) {
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
            .messages(messages)
            .model("gpt-3.5-turbo")
            .build();

        return service.createChatCompletion(completionRequest).getChoices();
    }

}
