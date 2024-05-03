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

package com.talkforgeai.backend.transformers;

import com.talkforgeai.backend.assistant.service.UniversalImageGenService;
import com.talkforgeai.backend.storage.FileStorageService;
import com.talkforgeai.backend.transformers.dto.TransformerContext;
import com.talkforgeai.service.plantuml.PlantUMLService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MessageProcessor {

  public static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessor.class);
  private final FileStorageService fileStorageService;
  List<Transformer> transformers = new ArrayList<>();

  public MessageProcessor(FileStorageService fileStorageService,
      PlantUMLService plantUMLService, UniversalImageGenService imageGenService) {
    this.fileStorageService = fileStorageService;

    transformers.add(new HtmlEncoderTransformer());
    transformers.add(new LaTeXTransformer());
    transformers.add(new PlantUMLTransformer(plantUMLService));
    transformers.add(new CodeBlockTransformer());
    transformers.add(new CodePhraseTransformer());
    transformers.add(new MarkdownHeaderTransformer());
//    transformers.add(new MarkdownListTransformer());
    transformers.add(new MarkdownTextTransformer());
    transformers.add(new NewLineTransformer());

    transformers.add(new ImageDownloadTransformer(imageGenService));
  }

  public String transform(String content, String threadId, String messageId) {
    if (content == null || content.isEmpty()) {
      return "";
    }

    String processedContent = content;

    TransformerContext context = new TransformerContext(
        threadId,
        messageId,
        fileStorageService.getAssistantsDirectory(),
        fileStorageService.getThreadDirectory()
    );

    for (Transformer t : transformers) {
      LOGGER.info("Transforming with {}...", t.getClass().getName());
      processedContent = t.process(processedContent, context);
    }

    LOGGER.info("Transformation done.");
    return processedContent;
  }

}
