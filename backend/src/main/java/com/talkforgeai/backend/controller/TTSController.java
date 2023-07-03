package com.talkforgeai.backend.controller;

import com.talkforgeai.backend.dto.TTSRequest;
import com.talkforgeai.backend.service.TTSService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


        return TTSService.streamVoice(request);
    }
}
