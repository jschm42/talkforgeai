package com.talkforgeai.talkforgeaiserver.controller;

import com.talkforgeai.talkforgeaiserver.dto.PersonaResponse;
import com.talkforgeai.talkforgeaiserver.service.PersonaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/persona")
public class PersonaController {
    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping
    public List<PersonaResponse> getAllPersona() {
        return personaService.getAllPersona();
    }

}
