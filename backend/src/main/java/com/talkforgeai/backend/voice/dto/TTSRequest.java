package com.talkforgeai.backend.voice.dto;

import java.util.UUID;

public record TTSRequest(String text, UUID personaId) {

}
