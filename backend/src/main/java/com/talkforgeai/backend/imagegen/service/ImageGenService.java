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

package com.talkforgeai.backend.imagegen.service;

import com.talkforgeai.backend.assistant.dto.ImageGenSystem;
import com.talkforgeai.backend.assistant.service.UniversalImageGenService;
import com.talkforgeai.backend.assistant.service.UniversalImageGenService.UniversalImageOptions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.stabilityai.api.StabilityAiApi;
import org.springframework.stereotype.Service;

@Service
public class ImageGenService {

  public static final Logger LOGGER = LoggerFactory.getLogger(ImageGenService.class);

  private final UniversalImageGenService universalImageGenService;

  ImageGenService(UniversalImageGenService universalImageGenService) {
    this.universalImageGenService = universalImageGenService;
  }

  public List<GeneratedImageResult> textToImage(String imagePrompt) {
//    UniversalImageOptions options = new UniversalImageOptions(
//        OpenAiImageApi.ImageModel.DALL_E_3.getValue(),
//        "",
//        1,
//        1024,
//        1024
//    );

    UniversalImageOptions options = new UniversalImageOptions(StabilityAiApi.DEFAULT_IMAGE_MODEL,
        "cinematic", 1, 512,
        512);

//    ImageResponse imageResponse = universalImageGenService.generate(ImageGenSystem.OPENAI,
//        imagePrompt,
//        options);
    ImageResponse imageResponse = universalImageGenService.generate(ImageGenSystem.STABILITY,
        imagePrompt,
        options);

    return imageResponse.getResults().stream()
        .map(i -> {
          Map<String, String> parsedMetadata = parseMetadata(i.getMetadata().toString());

          return new GeneratedImageResult(i.getOutput().getB64Json(),
              parsedMetadata.get("revisedPrompt"));
        })
        .toList();
  }

  private Map<String, String> parseMetadata(String metadata) {
    Map<String, String> keyValuePairs = new HashMap<>();
    Pattern pattern = Pattern.compile("(\\w+)='(.*?)'");
    Matcher matcher = pattern.matcher(metadata);

    while (matcher.find()) {
      String key = matcher.group(1);
      String value = matcher.group(2);
      keyValuePairs.put(key, value);
    }

    return keyValuePairs;
  }

  public record GeneratedImageResult(String base64Data, String imagePrompt) {

  }
}
