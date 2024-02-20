/*
 * Copyright (c) 2024 Jean Schmitz.
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

import com.theokanning.openai.audio.CreateTranscriptionRequest;
import com.theokanning.openai.audio.TranscriptionResult;
import com.theokanning.openai.service.OpenAiService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


/**
 * Service for Speech to Text (STT) operations.
 */
@Service
public class STTService {

  public static final Logger LOGGER = LoggerFactory.getLogger(STTService.class);


  private final OpenAiService openAiService;

  public STTService(OpenAiService openAiService) {
    this.openAiService = openAiService;
  }

  public ResponseEntity<String> convert(MultipartFile file, Path uploadDirectory) {
    try {
      Files.createDirectories(uploadDirectory);
      Path path = uploadDirectory.resolve("audio.wav");
      file.transferTo(path);
      String text = transscribeAudio(path.toFile());
      return ResponseEntity.ok(text);
    } catch (IOException e) {
      LOGGER.error("Failed to create directory or transfer file: {}", e.getMessage());
      return ResponseEntity.status(500).body("Error converting voice to text: " + e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Error while calling whisper API", e);
      return ResponseEntity.status(500).body("Error converting voice to text: " + e.getMessage());
    }
  }

  private String transscribeAudio(File file) {
    CreateTranscriptionRequest request = new CreateTranscriptionRequest();
    request.setModel("whisper-1");
    //request.setResponseFormat("text");
    // The language of the input audio. Supplying the input language in ISO-639-1 format will improve accuracy and latency.
    //request.setLanguage();

    TranscriptionResult transcription = openAiService.createTranscription(request, file);

    String responseText = transcription.getText();
    LOGGER.info("Extracted audio text: {}", responseText);
    return responseText;
  }

}
