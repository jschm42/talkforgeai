package com.talkforgeai.backend.voice.service;

import com.talkforgeai.backend.chat.PropertyKeys;
import com.talkforgeai.backend.persona.domain.PersonaEntity;
import com.talkforgeai.backend.persona.service.PersonaService;
import com.talkforgeai.backend.voice.dto.TTSRequest;
import com.talkforgeai.service.elevenlabs.ElevenLabsService;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsRequest;
import org.springframework.stereotype.Service;

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

        ElevenLabsRequest request = new ElevenLabsRequest(
                TTSRequest.text(),
                persona.getProperties().get(PropertyKeys.ELEVENLABS_VOICEID),
                persona.getProperties().get(PropertyKeys.ELEVENLABS_MODELID),
                new ElevenLabsRequest.VoiceSettings()
        );

        return elevenLabsService.stream(request);
    }

}
