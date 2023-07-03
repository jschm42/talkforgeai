package com.talkforgeai.backend.service;

import com.talkforgeai.backend.domain.PersonaEntity;
import com.talkforgeai.backend.domain.PropertyCategory;
import com.talkforgeai.backend.domain.PropertyEntity;
import com.talkforgeai.backend.dto.TTSRequest;
import com.talkforgeai.backend.elevenlabs.ElevenLabsService;
import com.talkforgeai.backend.elevenlabs.ElevenlabsRequestProperties;
import com.talkforgeai.backend.elevenlabs.dto.ElevenLabsRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TTSService {

    private final PersonaService personaService;
    private final ElevenLabsService elevenLabsService;

    public TTSService(ElevenLabsService elevenLabsService, PersonaService personaService) {
        this.personaService = personaService;
        this.elevenLabsService = elevenLabsService;
    }

    public byte[] streamVoice(TTSRequest TTSRequest) {
        PersonaEntity persona = personaService.getPersonaById(TTSRequest.personaId())
                .orElseThrow(() -> new RuntimeException("Persona not found:  " + TTSRequest.personaId()));

        Map<String, String> elevenlabsProperties = mapToElevenlabsProperties(persona.getProperties());

        ElevenLabsRequest request = new ElevenLabsRequest(
                TTSRequest.text(),
                elevenlabsProperties.get(ElevenlabsRequestProperties.VOICE_ID),
                elevenlabsProperties.get(ElevenlabsRequestProperties.MODEL_ID),
                new ElevenLabsRequest.VoiceSettings()
        );

        return elevenLabsService.stream(request);
    }

    private Map<String, String> mapToElevenlabsProperties(Map<String, PropertyEntity> properties) {
        Map<String, String> elevenlabsProperties = new HashMap<>();
        properties.forEach((key, value) -> {
            if (value.getCategory() == PropertyCategory.ELEVENLABS) {
                elevenlabsProperties.put(key, value.getPropertyValue());
            }
        });
        return elevenlabsProperties;
    }

}
