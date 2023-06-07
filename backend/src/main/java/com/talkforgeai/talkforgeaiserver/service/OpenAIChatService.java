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
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder().messages(messages).build();

        if (properties.containsKey(PropertyKeys.CHATGPT_MODEL)) {
            completionRequest.setModel(properties.get(PropertyKeys.CHATGPT_MODEL));
        }
        if (properties.containsKey(PropertyKeys.CHATGPT_MAX_TOKENS)) {
            completionRequest.setMaxTokens(Integer.valueOf(properties.get(PropertyKeys.CHATGPT_MAX_TOKENS)));
        }
        if (properties.containsKey(PropertyKeys.CHATGPT_TEMPERATURE)) {
            completionRequest.setTemperature(Double.valueOf(properties.get(PropertyKeys.CHATGPT_TEMPERATURE)));
        }
        if (properties.containsKey(PropertyKeys.CHATGPT_TOP_P)) {
            completionRequest.setTopP(Double.valueOf(properties.get(PropertyKeys.CHATGPT_TOP_P)));
        }
        if (properties.containsKey(PropertyKeys.CHATGPT_FREQUENCY_PENALTY)) {
            completionRequest.setFrequencyPenalty(Double.valueOf(properties.get(PropertyKeys.CHATGPT_FREQUENCY_PENALTY)));
        }
        if (properties.containsKey(PropertyKeys.CHATGPT_PRESENCE_PENALTY)) {
            completionRequest.setPresencePenalty(Double.valueOf(properties.get(PropertyKeys.CHATGPT_PRESENCE_PENALTY)));
        }

        return service.createChatCompletion(completionRequest).getChoices();
    }

}
