package com.talkforgeai.talkforgeaiserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.talkforgeaiserver.dto.ElevenLabsRequest;
import com.talkforgeai.talkforgeaiserver.dto.TTSRequest;
import com.talkforgeai.talkforgeaiserver.properties.ElevenlabsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class TTSService {
    private static final String VOICE_ID = "21m00Tcm4TlvDq8ikWAM";
    private final RestTemplate restTemplate;
    private final ElevenlabsProperties properties;
    Logger logger = LoggerFactory.getLogger(TTSService.class);

    public TTSService(ElevenlabsProperties properties) {
        this.restTemplate = new RestTemplate();
        this.properties = properties;
    }

    public byte[] speak(TTSRequest request) {
        // Prepare the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.ALL));
        headers.set("xi-api-key", properties.apiKey());

        ElevenLabsRequest ttsRequest = new ElevenLabsRequest();
        ttsRequest.setText(request.getText());
        ttsRequest.setModelId("eleven_multilingual_v1");

        ObjectMapper mapper = new ObjectMapper();
        String str = "";
        try {
            str = mapper.writeValueAsString(ttsRequest);
            logger.info("JSON: " + str);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpEntity<ElevenLabsRequest> requestEntity = new HttpEntity<>(ttsRequest, headers);

        // Send the request and get the response
        try {
            ResponseEntity<byte[]> response = restTemplate.postForEntity(
                    properties.apiUrl() + "/v1/text-to-speech/" + VOICE_ID + "/stream",
                    requestEntity,
                    byte[].class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("Error fetching audio stream.");
            }
        } catch (HttpClientErrorException ex) {
            throw new RuntimeException("Error fetching audio stream.", ex);
        }
    }
}
