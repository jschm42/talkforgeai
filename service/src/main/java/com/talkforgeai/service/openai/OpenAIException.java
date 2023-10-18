package com.talkforgeai.service.openai;

public class OpenAIException extends RuntimeException {
    private final String errorDetail;

    public OpenAIException(String message, String errorDetail) {
        super(message);
        this.errorDetail = errorDetail;
    }

    public String getErrorDetail() {
        return errorDetail;
    }
}
