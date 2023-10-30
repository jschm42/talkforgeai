/*
 * Copyright (c) 2023 Jean Schmitz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.talkforgeai.backend.voice.service;

import com.talkforgeai.backend.persona.domain.PersonaEntity;
import com.talkforgeai.backend.persona.service.PersonaProperties;
import com.talkforgeai.backend.persona.service.PersonaService;
import com.talkforgeai.backend.voice.dto.TTSRequest;
import com.talkforgeai.service.elevenlabs.ElevenLabsService;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsModel;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsRequest;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsVoicesResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TTSService {

    private final PersonaService personaService;
    private final ElevenLabsService elevenLabsService;

    public TTSService(ElevenLabsService elevenLabsService, PersonaService personaService) {
        this.personaService = personaService;
        this.elevenLabsService = elevenLabsService;
    }

    public byte[] streamElevenLabsVoice(TTSRequest TTSRequest) {
        PersonaEntity persona = personaService.getPersonaById(TTSRequest.personaId())
                .orElseThrow(() -> new RuntimeException("Persona not found:  " + TTSRequest.personaId()));

        ElevenLabsRequest request = new ElevenLabsRequest(
                TTSRequest.text(),
                persona.getProperties().get(PersonaProperties.ELEVENLABS_VOICEID.getKey()),
                persona.getProperties().get(PersonaProperties.ELEVENLABS_MODELID.getKey()),
                new ElevenLabsRequest.VoiceSettings()
        );

        return elevenLabsService.stream(request);
    }

    public List<ElevenLabsModel> getElevenLabsModels() {
        return elevenLabsService.getModels();
    }

    public ElevenLabsVoicesResponse getElevenLabsVoices() {
        return elevenLabsService.getVoices();
    }
}
