package com.talkforgeai.talkforgeaiserver.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.talkforgeaiserver.openai.dto.OpenAIRequest;
import com.talkforgeai.talkforgeaiserver.openai.dto.OpenAIResponse;
import com.talkforgeai.talkforgeaiserver.properties.OpenAIProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class OpenAIChatService {
    private final OpenAIProperties openAIProperties;
    Logger logger = LoggerFactory.getLogger(OpenAIChatService.class);

    public OpenAIChatService(OpenAIProperties openAIProperties) {
        this.openAIProperties = openAIProperties;
    }

    public OpenAIResponse submit(OpenAIRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + openAIProperties.apiKey());

        ObjectMapper objectMapper = new ObjectMapper();
        String message = null;
        try {
            message = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        logger.debug("Sending message: {}", message);

        HttpEntity<String> entity = new HttpEntity<>(message, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new LoggingRequestInterceptor());

        ResponseEntity<OpenAIResponse> response = restTemplate.exchange(openAIProperties.chatUrl(), HttpMethod.POST, entity, OpenAIResponse.class);
        return response.getBody();
    }
}
