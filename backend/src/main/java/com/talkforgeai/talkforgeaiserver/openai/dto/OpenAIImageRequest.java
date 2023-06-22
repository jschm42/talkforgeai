package com.talkforgeai.talkforgeaiserver.openai.dto;

public record OpenAIImageRequest(String prompt, int n, String size) {

    public OpenAIImageRequest(String prompt) {
        this(prompt, 2, "512x512");
    }
}
