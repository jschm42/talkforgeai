package com.talkforgeai.service.plantuml;

import net.sourceforge.plantuml.SourceStringReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class PlantUMLService {
    public static final Logger LOGGER = LoggerFactory.getLogger(PlantUMLService.class);

    public void generateUmlDiagram(String source, String fileName) {
        SourceStringReader reader = new SourceStringReader(source);
        try (OutputStream png = new FileOutputStream(fileName)) {
            // Write the first image to "png"
            String s = reader.generateImage(png);
            LOGGER.info("Generated UML diagram: {}", s);
        } catch (IOException e) {
            LOGGER.error("Error generating UML diagram: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
