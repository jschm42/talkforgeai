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

package com.talkforgeai.backend.voice.controller;

import com.talkforgeai.backend.voice.dto.TTSRequest;
import com.talkforgeai.backend.voice.service.TextToSpeechService;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsModel;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsVoicesResponse;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tts")
public class TextToSpeechController {

  private final TextToSpeechService textToSpeechService;

  public TextToSpeechController(TextToSpeechService textToSpeechService) {
    this.textToSpeechService = textToSpeechService;
  }

  @PostMapping(value = "/stream",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public byte[] speak(@RequestBody TTSRequest request) {

    return textToSpeechService.streamElevenLabsVoice(request);
  }

  @GetMapping(value = "/models")
  public List<ElevenLabsModel> getElevenLabsModels() {
    return textToSpeechService.getElevenLabsModels();
  }

  @GetMapping(value = "/voices")
  public ElevenLabsVoicesResponse getElevenLabsVoices() {
    return textToSpeechService.getElevenLabsVoices();
  }
}
