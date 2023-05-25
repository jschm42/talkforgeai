package com.talkforgeai.talkforgeaiserver.controller;

import com.talkforgeai.talkforgeaiserver.dto.Role;
import com.talkforgeai.talkforgeaiserver.service.ChatGptResponse;
import com.talkforgeai.talkforgeaiserver.service.OpenAIChatService;
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
    public ChatGptResponse submit(@RequestBody String prompt) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(Role.user.name(), prompt));
        return openAIChatService.submit(messages);
    }

}
