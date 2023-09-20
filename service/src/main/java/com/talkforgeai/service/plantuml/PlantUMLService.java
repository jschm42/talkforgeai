package com.talkforgeai.service.plantuml;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.core.DiagramDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class PlantUMLService {
    public static final Logger LOGGER = LoggerFactory.getLogger(PlantUMLService.class);

    public DiagramDescription generateUmlDiagram(String source, String fileName) {
        SourceStringReader reader = new SourceStringReader(source);
        try (OutputStream png = new FileOutputStream(fileName)) {

            FileFormatOption option = new FileFormatOption(FileFormat.PNG)
                    .withScale(2.0);

            // Write the first image to "png"
            DiagramDescription diagramDescription = reader.outputImage(png, option);
            LOGGER.info("Generated UML diagram: {}", diagramDescription);
            return diagramDescription;
        } catch (IOException e) {
            LOGGER.error("Error generating UML diagram: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
