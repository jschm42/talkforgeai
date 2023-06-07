package com.talkforgeai.talkforgeaiserver.service;

import com.talkforgeai.talkforgeaiserver.dto.PropertyKeys;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OpenAIChatService {

    private final OpenAiService service;

    public OpenAIChatService(OpenAiService service) {
        this.service = service;
    }

    public List<ChatCompletionChoice> submit(List<ChatMessage> messages, Map<String, String> properties) {
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .messages(messages)
                .model(properties.get(PropertyKeys.CHATGPT_MODEL))
                .maxTokens(Integer.valueOf(properties.get(PropertyKeys.CHATGPT_MAX_TOKENS)))
                .temperature(Double.valueOf(properties.get(PropertyKeys.CHATGPT_TEMPERATURE)))
                .topP(Double.valueOf(properties.get(PropertyKeys.CHATGPT_TOP_P)))
                .frequencyPenalty(Double.valueOf(properties.get(PropertyKeys.CHATGPT_FREQUENCY_PENALTY)))
                .presencePenalty(Double.valueOf(properties.get(PropertyKeys.CHATGPT_PRESENCE_PENALTY)))
                .build();

        return service.createChatCompletion(completionRequest).getChoices();
    }

}
