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

package com.talkforgeai.backend.assistant.service;

import com.talkforgeai.backend.assistant.dto.ImageGenSystem;
import com.talkforgeai.backend.assistant.exception.AssistentException;
import org.springframework.ai.image.ImageClient;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageClient;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.ai.stabilityai.StabilityAiImageClient;
import org.springframework.ai.stabilityai.api.StabilityAiApi;
import org.springframework.ai.stabilityai.api.StabilityAiImageOptions;
import org.springframework.stereotype.Service;

@Service
public class UniversalImageGenService {

  private final OpenAiImageClient openAiImageClient;

  private final StabilityAiImageClient stabilityAiImageClient;

  public UniversalImageGenService(OpenAiImageClient openAiImageClient,
      StabilityAiImageClient stabilityAiImageClient) {
    this.openAiImageClient = openAiImageClient;
    this.stabilityAiImageClient = stabilityAiImageClient;
  }

  public ImageResponse generate(ImageGenSystem imageGenSystem, String text) {
    return generate(imageGenSystem, text, getDefaultUniversalImageOptions(imageGenSystem));
  }

  public ImageResponse generate(ImageGenSystem imageGenSystem, String text,
      UniversalImageOptions universalImageOptions) {
    ImagePrompt imagePrompt = new ImagePrompt(text,
        getImageOptions(imageGenSystem, universalImageOptions));

    return getClient(imageGenSystem).call(imagePrompt);
  }


  UniversalImageOptions getDefaultUniversalImageOptions(ImageGenSystem system) {
    switch (system) {
      case OPENAI -> {
        return new UniversalImageOptions(OpenAiImageApi.ImageModel.DALL_E_3.getValue(), "hd", 1,
            1024, 1024);
      }
      case STABILITY -> {
        return new UniversalImageOptions(StabilityAiApi.DEFAULT_IMAGE_MODEL, "cinematic", 1, 512,
            512);
      }
      default -> throw new AssistentException("Image generation system not supported: " + system);
    }
  }

  ImageOptions getImageOptions(ImageGenSystem system, UniversalImageOptions universalImageOptions) {
    switch (system) {
      case OPENAI -> {
        return OpenAiImageOptions.builder()
            .withModel(universalImageOptions.model)
            .withQuality("hd")
            .withN(universalImageOptions.n())
            .withHeight(universalImageOptions.height())
            .withWidth(universalImageOptions.width())
            .build();
      }
      case STABILITY -> {
        return StabilityAiImageOptions.builder()
            .withModel(universalImageOptions.model)
            .withStylePreset("cinematic")
            .withN(universalImageOptions.n())
            .withHeight(universalImageOptions.height())
            .withWidth(universalImageOptions.width())
            .build();
      }
      default -> throw new AssistentException("Image generation system not supported: " + system);
    }
  }

  private ImageClient getClient(ImageGenSystem system) {
    switch (system) {
      case OPENAI -> {
        return openAiImageClient;
      }
      case STABILITY -> {
        return stabilityAiImageClient;
      }
      default -> throw new IllegalStateException("Unexpected image gen system: " + system);
    }
  }

  public static record UniversalImageOptions(String model, String quality, int n, int height,
                                             int width) {

  }

}
