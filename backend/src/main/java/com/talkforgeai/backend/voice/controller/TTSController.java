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

package com.talkforgeai.backend.voice.controller;

import com.talkforgeai.backend.voice.dto.TTSRequest;
import com.talkforgeai.backend.voice.service.TTSService;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsModel;
import com.talkforgeai.service.elevenlabs.dto.ElevenLabsVoicesResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tts")
public class TTSController {

    private final TTSService TTSService;

    public TTSController(TTSService TTSService) {
        this.TTSService = TTSService;
    }

    @PostMapping(value = "/stream",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] speak(@RequestBody TTSRequest request) {


        return TTSService.streamElevenLabsVoice(request);
    }

    @GetMapping(value = "/models")
    public List<ElevenLabsModel> getElevenLabsModels() {
        return TTSService.getElevenLabsModels();
    }

    @GetMapping(value = "/voices")
    public ElevenLabsVoicesResponse getElevenLabsVoices() {
        return TTSService.getElevenLabsVoices();
    }
}
