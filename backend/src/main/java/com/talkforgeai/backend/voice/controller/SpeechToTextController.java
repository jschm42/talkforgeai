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

import com.talkforgeai.backend.storage.FileStorageService;
import com.talkforgeai.backend.voice.service.SpeechToTextService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController()
@RequestMapping("/api/v1/stt")
public class SpeechToTextController {

  private final SpeechToTextService speechToTextService;

  private final FileStorageService fileStorageService;

  public SpeechToTextController(SpeechToTextService speechToTextService,
      FileStorageService fileStorageService) {
    this.speechToTextService = speechToTextService;
    this.fileStorageService = fileStorageService;
  }

  @PostMapping("/convert")
  public ResponseEntity<String> convert(@RequestParam("file") MultipartFile file) {
    return speechToTextService.convert(file,
        fileStorageService.getDataDirectory().resolve("uploads"));
  }
}
