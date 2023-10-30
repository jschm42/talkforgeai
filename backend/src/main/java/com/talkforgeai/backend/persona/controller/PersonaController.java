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

package com.talkforgeai.backend.persona.controller;

import com.talkforgeai.backend.persona.dto.PersonaDto;
import com.talkforgeai.backend.persona.dto.PersonaImageUploadResponse;
import com.talkforgeai.backend.persona.service.PersonaService;
import com.talkforgeai.backend.storage.FileStorageService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

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

    @GetMapping("/{personaId}")
    public PersonaDto getPersona(@PathVariable("personaId") UUID personaId) {
        return personaService.getPersona(personaId);
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

    @PostMapping("/image/generate")
    public GenerateImageResponse generateImage(@RequestBody GenerateImageRequest generateImageRequest) throws IOException {
        return personaService.generateImage(generateImageRequest.prompt());
    }

    @PostMapping
    public void updatePersona(@RequestBody PersonaDto personaDto) {
        personaService.updatePersona(personaDto);
    }

    @PostMapping("/upload")
    public PersonaImageUploadResponse singleFileUpload(@RequestParam("file") MultipartFile file) {
        return personaService.uploadImage(file);
    }
}
