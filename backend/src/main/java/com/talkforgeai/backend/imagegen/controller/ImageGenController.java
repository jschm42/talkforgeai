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

package com.talkforgeai.backend.imagegen.controller;

import com.talkforgeai.backend.imagegen.dto.TextToImageRequest;
import com.talkforgeai.backend.imagegen.service.ImageGenService;
import com.talkforgeai.backend.imagegen.service.ImageGenService.GeneratedImageResult;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/imagegen")
public class ImageGenController {

  private final ImageGenService imageGenService;

  public ImageGenController(ImageGenService imageGenService) {
    this.imageGenService = imageGenService;
  }

  @PostMapping("/text2image")
  public List<GeneratedImageResult> textToImage(@RequestBody TextToImageRequest request) {
    return imageGenService.textToImage(request.imagePrompt());
  }

}
