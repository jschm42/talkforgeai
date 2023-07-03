package com.talkforgeai.backend.transformers.dto;

import java.nio.file.Path;
import java.util.UUID;

public record TransformerContext(UUID sessionId, Path dataDirectory) {
}
