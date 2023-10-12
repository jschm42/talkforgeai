package com.talkforgeai.backend.persona.controller;

import com.talkforgeai.backend.persona.dto.PersonaDto;
import com.talkforgeai.backend.persona.service.PersonaService;
import com.talkforgeai.backend.storage.FileStorageService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/v1/persona")
public class PersonaController {
    private final PersonaService personaService;
    private final FileStorageService fileStorageService;

    public PersonaController(PersonaService personaService, FileStorageService fileStorageService) {
        this.personaService = personaService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public List<PersonaDto> getAllPersona() {
        return personaService.getAllPersona();
    }

    @GetMapping("/image/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            Path imgFilePath = fileStorageService.getPersonaDirectory().resolve(filename);
            Resource resource = new FileSystemResource(imgFilePath);
            byte[] imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public void createPersona(@RequestBody PersonaDto personaDto) {
        personaService.createPersona(personaDto);
    }

}
