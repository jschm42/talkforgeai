package com.talkforgeai.talkforgeaiserver.dto;

import java.util.UUID;

public record ChatCompletionRequest(UUID sessionId,
                                    String prompt) {
}
