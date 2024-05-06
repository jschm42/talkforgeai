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

import com.talkforgeai.backend.service.UniqueIdGenerator;
import com.talkforgeai.backend.voice.dto.TranscriptionSystem;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


/**
 * Service for Speech to Text (STT) operations.
 */
@Service
public class SpeechToTextService {

  public static final Logger LOGGER = LoggerFactory.getLogger(SpeechToTextService.class);

  private final UniversalTranscriptionService universalTranscriptionService;

  private final UniqueIdGenerator uniqueIdGenerator;

  public SpeechToTextService(UniversalTranscriptionService universalTranscriptionService,
      UniqueIdGenerator uniqueIdGenerator) {
    this.universalTranscriptionService = universalTranscriptionService;
    this.uniqueIdGenerator = uniqueIdGenerator;
  }

  public ResponseEntity<String> convert(MultipartFile file, Path tempDirectory) {
    try {
      Files.createDirectories(tempDirectory);
      Path audioFile = tempDirectory.resolve(uniqueIdGenerator.generateAudioId() + ".wav");
      file.transferTo(audioFile);
      String text = transscribeAudio(audioFile.toFile());
      Files.delete(audioFile);
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
    AudioTranscriptionResponse transcriptionResponse = universalTranscriptionService.transcribe(
        TranscriptionSystem.OPENAI, file);

    String responseText = transcriptionResponse.getResult().getOutput();
    LOGGER.info("Extracted audio text: {}", responseText);
    return responseText;
  }

}
