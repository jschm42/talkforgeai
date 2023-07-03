package com.talkforgeai.talkforgeaiserver.dto;

import java.util.UUID;

public record TTSRequest(String text, UUID personaId) {

}
