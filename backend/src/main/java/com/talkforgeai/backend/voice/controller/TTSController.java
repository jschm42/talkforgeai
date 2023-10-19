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
