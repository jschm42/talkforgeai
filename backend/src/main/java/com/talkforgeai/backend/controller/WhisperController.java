package com.talkforgeai.backend.controller;

import com.talkforgeai.backend.service.WhisperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class WhisperController {

    private final WhisperService whisperService;

    public WhisperController(WhisperService whisperService) {
        this.whisperService = whisperService;
    }

    @PostMapping("/api/convert")
    public ResponseEntity<String> convert(@RequestParam("file") MultipartFile file) {
        return whisperService.convert(file);
    }
}
