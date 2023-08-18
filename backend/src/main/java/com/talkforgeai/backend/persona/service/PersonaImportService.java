package com.talkforgeai.backend.persona.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.backend.persona.domain.PersonaEntity;
import com.talkforgeai.backend.persona.domain.PropertyCategory;
import com.talkforgeai.backend.persona.domain.PropertyEntity;
import com.talkforgeai.backend.persona.domain.PropertyType;
import com.talkforgeai.backend.persona.dto.PersonaImport;
import com.talkforgeai.backend.persona.repository.PersonaRepository;
import com.talkforgeai.backend.storage.FileStorageService;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PersonaImportService {
    public static final Logger LOGGER = LoggerFactory.getLogger(PersonaImportService.class);
    private final ResourcePatternResolver resourcePatternResolver;

    private final FileStorageService fileStorageService;

    private final ResourcePatternResolver resourceResolver;

    private final PersonaRepository personaRepository;

    private Map<PropertyCategory, Map<String, PropertyType>> propertyTypes = Map.of(
            PropertyCategory.CHATGPT, Map.of(
                    "model", PropertyType.STRING,
                    "temperature", PropertyType.DOUBLE,
                    "top_p", PropertyType.DOUBLE
            ),
            PropertyCategory.ELEVENLABS, Map.of(
                    "voiceId", PropertyType.STRING
            )
    );

    public PersonaImportService(FileStorageService fileStorageService, PersonaRepository personaRepository, ResourcePatternResolver resourceResolver) {
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
        this.fileStorageService = fileStorageService;
        this.personaRepository = personaRepository;
        this.resourceResolver = resourceResolver;
    }

    @Transactional
    public void importPersona() {
        ObjectMapper mapper = new ObjectMapper();
        List<PersonaEntity> personas = new ArrayList<>();

        try {
            LOGGER.info("Importing persona files.");

            Path personaImportDirectory = fileStorageService.getPersonaImportDirectory();

            // Read persona json files from import folder
            importPersonaFromImportDirectory(personaImportDirectory, mapper, personas);

            // Read persona json files from classpath:persona-import/
            importPersonaFromResource(mapper, personas);

            savePersona(personas);

        } catch (IOException e) {
            LOGGER.error("Error during import of persona.", e);
        }

        LOGGER.info("Copying images from import directory.");
        copyPersonaImagesFromImportDirectory();
        copyPersonaImagesFromResources();

        LOGGER.info("Import done.");
    }

    private void savePersona(List<PersonaEntity> personas) {
        personas.forEach(personaEntity -> {
            boolean isExistsPersona = personaRepository.existsByName(personaEntity.getName());

            if (isExistsPersona) {
                LOGGER.info("Persona {} exists. Import skipped.", personaEntity.getName());
            } else {
                LOGGER.info("Importing persona {}", personaEntity.getName());
                personaRepository.save(personaEntity);
            }
        });
    }

    private void importPersonaFromResource(ObjectMapper mapper, List<PersonaEntity> personas) throws IOException {
        Resource[] resources = resourcePatternResolver.getResources("classpath:persona-import/*.json");
        for (Resource resource : resources) {
            LOGGER.info("Found persona {}", resource.getFilename());

            InputStream inputStream = resource.getInputStream();
            PersonaEntity persona = getPersonaEntity(inputStream);
            personas.add(persona);
        }
    }

    private void importPersonaFromImportDirectory(Path personaImportDirectory, ObjectMapper mapper, List<PersonaEntity> personas) throws IOException {
        try (DirectoryStream<Path> directoryStream =
                     Files.newDirectoryStream(personaImportDirectory, "*.json")) {
            for (Path path : directoryStream) {
                LOGGER.info("Found persona {}", path.getFileName().toString());

                InputStream inputStream = Files.newInputStream(path);
                PersonaEntity persona = getPersonaEntity(inputStream);
                personas.add(persona);
            }
        }
    }

    private void copyPersonaImagesFromResources() {
        // The prefix "classpath*:" allows resources to be loaded from the classpath, matching the given pattern
        String personaImgPattern = "classpath*:persona-import/*.{jpg,png}";

        try {
            // Get Resource array for the static folder
            Resource[] imageResources = resourceResolver.getResources(personaImgPattern);

            // Iterate each image resource and copy to target destination
            for (Resource imageResource : imageResources) {
                if (imageResource.isReadable()) {
                    try (InputStream resourceStream = imageResource.getInputStream()) {
                        // You need to redefine your copyImage method to accept InputStream
                        String imageName = imageResource.getFilename();
                        copyImage(resourceStream, imageName);
                    } catch (IOException e) {
                        LOGGER.error("Failed to read image resource.", e);
                    }
                }
            }

        } catch (IOException e) {
            LOGGER.error("Failed to access persona resources.", e);
        }
    }

    private void copyPersonaImagesFromImportDirectory() {
        Path personaImportDirectory = fileStorageService.getPersonaImportDirectory();

        // Copy images from the personaImportDirectory
        try (var stream = Files.walk(personaImportDirectory)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".png") || path.toString().endsWith(".jpg"))
                    .forEach(this::copyImage);
        } catch (IOException e) {
            LOGGER.error("Failed to access persona import directory.", e);
        }
    }

    private void copyImage(Path imagePath) {
        try {
            Path targetPath = fileStorageService.getPersonaDirectory().resolve(imagePath.getFileName());

            if (!Files.exists(targetPath)) {
                LOGGER.info("Copying image {}", imagePath);
                Files.copy(imagePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                LOGGER.info("Skipping copying image {}. File already exists in the target directory.", imagePath);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to copy image {}", imagePath, e);
        }
    }

    private void copyImage(InputStream imageStream, String imageName) {
        try {
            Path targetPath = fileStorageService.getPersonaDirectory().resolve(imageName);

            if (!Files.exists(targetPath)) {
                LOGGER.info("Copying image {}", imageName);
                Files.copy(imageStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                LOGGER.info("Skipping copying image {}. File already exists in the target directory.", imageName);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to copy image {}", imageName, e);
        }
    }

    @NotNull
    private PersonaEntity getPersonaEntity(InputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        PersonaImport persona = mapper.readValue(inputStream, PersonaImport.class);

        PersonaEntity personaEntity = new PersonaEntity();
        personaEntity.setName(persona.name());
        personaEntity.setDescription(persona.description());
        personaEntity.setGlobalSystems(persona.globalSystems());
        personaEntity.setSystem(persona.system());
        personaEntity.setRequestFunctions(persona.requestFunctions());
        personaEntity.setImagePath(persona.imagePath());

        Map<String, PropertyEntity> properties = new HashMap<>();
        properties.put("model", createPropertyEntity("model", persona.chatGptConfig().model(), PropertyCategory.CHATGPT));
        properties.put("temperature", createPropertyEntity("temperature", persona.chatGptConfig().temperature(), PropertyCategory.CHATGPT));
        properties.put("top_p", createPropertyEntity("top_p", persona.chatGptConfig().topP(), PropertyCategory.CHATGPT));
        properties.put("voiceId", createPropertyEntity("voiceId", persona.elevenLabsConfig().voiceId(), PropertyCategory.ELEVENLABS));

        personaEntity.setProperties(properties);

        return personaEntity;
    }

    private PropertyEntity createPropertyEntity(String key, String value, PropertyCategory category) {
        PropertyEntity propertyEntity = new PropertyEntity();
        propertyEntity.setPropertyKey(key);
        propertyEntity.setPropertyValue(value);
        propertyEntity.setCategory(category);
        propertyEntity.setType(getPropertyType(key, category));
        return propertyEntity;
    }

    private PropertyType getPropertyType(String key, PropertyCategory category) {
        Map<String, PropertyType> types = propertyTypes.get(category);

        if (types.containsKey(key)) {
            return types.get(key);
        }

        throw new IllegalArgumentException("Unknown key '" + key + "' with category '" + category + "'.");
    }
}
