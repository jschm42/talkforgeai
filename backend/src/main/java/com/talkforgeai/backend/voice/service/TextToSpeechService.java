/*
 * Copyright (c) 2023-2024 Jean Schmitz.
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

import com.talkforgeai.backend.assistant.dto.AssistantDto;
import com.talkforgeai.backend.assistant.service.AssistantProperty;
import com.talkforgeai.backend.assistant.service.AssistantSpringService;
import com.talkforgeai.backend.voice.dto.TTSRequest;
import com.talkforgeai.service.elevenlabs.ElevenLabsService;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsModel;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsRequest;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsVoicesResponse;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TextToSpeechService {

  public static final Logger LOGGER = LoggerFactory.getLogger(TextToSpeechService.class);

  private final ElevenLabsService elevenLabsService;
  private final AssistantSpringService assistantService;

  public TextToSpeechService(ElevenLabsService elevenLabsService,
      AssistantSpringService assistantService) {
    this.assistantService = assistantService;
    this.elevenLabsService = elevenLabsService;
  }

  public byte[] streamElevenLabsVoice(TTSRequest TTSRequest) {
    AssistantDto assistantDto = assistantService.retrieveAssistant(TTSRequest.assistantId());
    Map<String, String> assistantProperties = assistantDto.properties();

    ElevenLabsRequest request = new ElevenLabsRequest(
        TTSRequest.text(),
        assistantProperties.get(AssistantProperty.ELEVENLABS_VOICEID.getKey()),
        assistantProperties.get(AssistantProperty.ELEVENLABS_MODELID.getKey()),
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
