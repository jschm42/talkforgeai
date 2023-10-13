package com.talkforgeai.backend.persona.service;

import com.talkforgeai.backend.persona.domain.PersonaEntity;
import com.talkforgeai.backend.persona.dto.PersonaDto;
import com.talkforgeai.backend.persona.repository.PersonaRepository;
import com.talkforgeai.backend.storage.FileStorageService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PersonaService {
    public static final Logger LOGGER = LoggerFactory.getLogger(PersonaService.class);
    private final PersonaRepository personaRepository;
    private final PersonaMapper personaMapper;

    private final FileStorageService fileStorageService;

    public PersonaService(PersonaRepository personaRepository, PersonaMapper personaMapper, FileStorageService fileStorageService) {
        this.personaRepository = personaRepository;
        this.personaMapper = personaMapper;
        this.fileStorageService = fileStorageService;
    }

    public List<PersonaDto> getAllPersona() {
        return getPersonaResponse(personaRepository.findAll());
    }

    public PersonaDto getPersona(UUID personaId) {
        Optional<PersonaEntity> personaById = this.getPersonaById(personaId);

        if (personaById.isPresent()) {
            return personaMapper.mapPersonaResponse(personaById.get());
        }

        throw new IllegalArgumentException("Persona with id " + personaId + " not found");
    }

    public Optional<PersonaEntity> getPersonaById(UUID personaId) {
        return personaRepository.findById(personaId);
    }

    public Optional<PersonaEntity> getPersonaByName(String personaName) {
        return personaRepository.findByName(personaName);
    }

    public List<PersonaDto> getPersonaResponse(List<PersonaEntity> personaEntities) {
        return personaEntities.stream().map(personaMapper::mapPersonaResponse).toList();
    }

    @Transactional
    public void updatePersona(PersonaDto personaDto) {
        LOGGER.info("Updating persona {}", personaDto);
        personaRepository.save(personaMapper.mapPersonaDto(personaDto));
    }

    public String uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            return "Please select a file to upload";
        }

        try {
            byte[] bytes = file.getBytes();
            Path path = fileStorageService.getPersonaDirectory().resolve(file.getOriginalFilename());
            Files.write(path, bytes);

            return "You successfully uploaded '" + file.getOriginalFilename() + "'";

        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload file";
        }
    }
}
