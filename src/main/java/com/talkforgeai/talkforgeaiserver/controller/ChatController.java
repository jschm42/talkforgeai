package com.talkforgeai.talkforgeaiserver.controller;

import com.talkforgeai.talkforgeaiserver.service.OpenAIChatService;
import com.talkforgeai.talkforgeaiserver.service.dto.ChatCompletionRequest;
import com.talkforgeai.talkforgeaiserver.service.dto.ChatCompletionResponse;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ChatController {

    private final OpenAIChatService openAIChatService;

    public ChatController(OpenAIChatService openAIChatService) {
        this.openAIChatService = openAIChatService;
    }

    @PostMapping("/chat")
    public ChatCompletionResponse submit(@RequestBody ChatCompletionRequest request) {
        List<ChatMessage> messages = List.of(new ChatMessage("user", request.prompt()));

        List<ChatCompletionChoice> choices = openAIChatService.submit(messages);

        choices.forEach(System.out::println);

        return new ChatCompletionResponse(new ArrayList<>());
    }

}
