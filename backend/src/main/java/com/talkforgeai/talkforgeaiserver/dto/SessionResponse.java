package com.talkforgeai.talkforgeaiserver.dto;

import java.util.UUID;

public record SessionResponse(UUID id, String title, String description) {

}
