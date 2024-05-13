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

import com.talkforgeai.backend.transformers.dto.TransformerContext;
import com.talkforgeai.service.plantuml.PlantUMLService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.plantuml.core.DiagramDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PlantUMLTransformer implements Transformer {

  static final String IMAGE_URL = "/api/v1/threads/%s/%s";

  static final String TEMPLATE = """
      %s
      <div class="card shadow">
        <div class="card-body">
          <img src='%s' title='%s'>
        </div>
      </div>
      %s
      """;
  private static final Logger LOGGER = LoggerFactory.getLogger(PlantUMLTransformer.class);
  private static final Pattern UrlRegEx = Pattern.compile("```(.*)\n@startuml\n([\\s\\S]*?)```",
      Pattern.MULTILINE);
  private final PlantUMLService service;

  public PlantUMLTransformer(PlantUMLService service) {
    this.service = service;
  }

  @Override
  public String process(String content, TransformerContext context) {
    Matcher matcher = UrlRegEx.matcher(content);

    while (matcher.find()) {
      String fullTag = matcher.group(0);
      String lang = matcher.group(1);
      String code = "@startuml\n" + matcher.group(2);

      String fileName = UUID.randomUUID() + "_plantuml.png";
      Path subDirectoryPath = context.threadDirectory().resolve(context.threadId());
      Path localFilePath = subDirectoryPath.resolve(fileName);

      // Ensure the directory exists and is writable
      try {
        if (!Files.exists(subDirectoryPath)) {
          Files.createDirectories(subDirectoryPath);
        }
        if (!Files.isWritable(subDirectoryPath)) {
          throw new IOException("Directory is not writable: " + subDirectoryPath);
        }
      } catch (IOException ioEx) {
        throw new RuntimeException(ioEx);
      }

      // HTML decode the code
      code = code.replace("&lt;", "<").replace("&gt;", ">");
      
      DiagramDescription diagramDescription = service.generateUmlDiagram(code,
          localFilePath.toString());
      LOGGER.info("Generated PlantUML diagram: {}", diagramDescription.getDescription());

      String imageUrl = String.format(IMAGE_URL, context.threadId(), fileName);

      // Perform your Mustache template replacement here
      String formattedContent = TEMPLATE.formatted(NO_LB_MARKER_START, imageUrl, code,
          NO_LB_MARKER_END);
      content = content.replace(fullTag, formattedContent);
    }

    return content;
  }

}
