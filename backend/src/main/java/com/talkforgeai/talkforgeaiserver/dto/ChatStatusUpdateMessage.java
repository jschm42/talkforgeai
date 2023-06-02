package com.talkforgeai.talkforgeaiserver.dto;

import java.util.UUID;

public record ChatStatusUpdateMessage(UUID sessionId, String message) {

}
