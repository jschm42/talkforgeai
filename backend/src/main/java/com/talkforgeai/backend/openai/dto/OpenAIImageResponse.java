package com.talkforgeai.backend.openai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public record OpenAIImageResponse(Date created, List<ImageData> data) {

    public record ImageData(String url,
                            @JsonProperty("b64_json") String b64Json) {
    }
}
