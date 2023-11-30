/*
 * Copyright (c) 2023 Jean Schmitz.
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

package com.talkforgeai.service.plantuml;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.core.DiagramDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PlantUMLService {

  public static final Logger LOGGER = LoggerFactory.getLogger(PlantUMLService.class);

  public DiagramDescription generateUmlDiagram(String source, String fileName) {
    SourceStringReader reader = new SourceStringReader(source);
    try (OutputStream png = new FileOutputStream(fileName)) {

      FileFormatOption option = new FileFormatOption(FileFormat.PNG).withScale(2.0);

      // Write the first image to "png"
      DiagramDescription diagramDescription = reader.outputImage(png, option);
      LOGGER.info("Generated UML diagram: {}", diagramDescription);
      return diagramDescription;
    } catch (IOException e) {
      throw new PlantUMLException("Error generating PlantUML diagram.", e);
    }
  }
}
