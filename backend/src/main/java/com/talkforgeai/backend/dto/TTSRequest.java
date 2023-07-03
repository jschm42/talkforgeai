package com.talkforgeai.backend.dto;

import java.util.UUID;

public record TTSRequest(String text, UUID personaId) {

}
