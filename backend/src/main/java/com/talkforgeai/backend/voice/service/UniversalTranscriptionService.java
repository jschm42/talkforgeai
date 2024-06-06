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

import com.talkforgeai.backend.assistant.exception.AssistentException;
import com.talkforgeai.backend.voice.dto.TranscriptionSystem;
import java.io.File;
import org.springframework.ai.model.Model;
import org.springframework.ai.model.ModelOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi.TranscriptResponseFormat;
import org.springframework.ai.openai.api.OpenAiAudioApi.WhisperModel;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

@Service
public class UniversalTranscriptionService {

  private final OpenAiAudioTranscriptionModel openAiTranscriptionModel;


  public UniversalTranscriptionService(OpenAiAudioTranscriptionModel openAiTranscriptionModel) {
    this.openAiTranscriptionModel = openAiTranscriptionModel;
  }

  ModelOptions getDefaultTranscriptionOptions(TranscriptionSystem system) {
    switch (system) {
      case OPENAI -> {
        return OpenAiAudioTranscriptionOptions.builder()
            .withModel(WhisperModel.WHISPER_1.getValue())
            .withLanguage("en")
            .withResponseFormat(TranscriptResponseFormat.TEXT)
            .withTemperature(0.5f)
            .build();
      }
      default -> throw new AssistentException("Transcription system not supported: " + system);
    }
  }

  ModelOptions getTranscriptionOptions(TranscriptionSystem system,
      UniversalTranscriptionOptions universalTranscriptionOptions) {
    switch (system) {
      case OPENAI -> {
        return OpenAiAudioTranscriptionOptions.builder()
            .withModel(universalTranscriptionOptions.model())
            .withLanguage("en")
            .withResponseFormat(TranscriptResponseFormat.TEXT)
            .withTemperature(universalTranscriptionOptions.temperature())
            .build();
      }
      default -> throw new AssistentException("Image generation system not supported: " + system);
    }
  }


  public AudioTranscriptionResponse transcribe(TranscriptionSystem system, File audioFile) {
    var audioResource = new FileSystemResource(audioFile.getAbsolutePath());

    AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource,
        getDefaultTranscriptionOptions(system));

    return getClient(system).call(prompt);
  }

  public AudioTranscriptionResponse transcribe(TranscriptionSystem system, File audioFile,
      UniversalTranscriptionOptions options) {
    var audioResource = new FileSystemResource(audioFile.getAbsolutePath());

    AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource,
        getTranscriptionOptions(system, options));

    return getClient(system).call(prompt);
  }


  private Model<AudioTranscriptionPrompt, AudioTranscriptionResponse> getClient(
      TranscriptionSystem system) {
    switch (system) {
      case OPENAI -> {
        return openAiTranscriptionModel;
      }
      default -> throw new IllegalStateException("Unexpected transcription system: " + system);
    }
  }

  public static record UniversalTranscriptionOptions(String model, String language,
                                                     float temperature) {

  }
}
