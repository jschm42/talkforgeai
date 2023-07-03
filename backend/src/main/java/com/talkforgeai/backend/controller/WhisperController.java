package com.talkforgeai.backend.controller;

import com.talkforgeai.backend.storage.FileStorageService;
import com.talkforgeai.service.openai.OpenAIWhisperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class WhisperController {

    private final OpenAIWhisperService openAIWhisperService;

    @Autowired
    private final FileStorageService fileStorageService;

    public WhisperController(OpenAIWhisperService openAIWhisperService, FileStorageService fileStorageService) {
        this.openAIWhisperService = openAIWhisperService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/api/convert")
    public ResponseEntity<String> convert(@RequestParam("file") MultipartFile file) {
        return openAIWhisperService.convert(file, fileStorageService.getDataDirectory().resolve("uploads"));
    }
}
