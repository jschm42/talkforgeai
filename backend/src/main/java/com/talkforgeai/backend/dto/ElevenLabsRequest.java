package com.talkforgeai.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ElevenLabsRequest {
    private String text;

    @JsonProperty("model_id")
    private String modelId;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    @Override
    public String toString() {
        return "ElevenLabsRequest{" +
                "text='" + text + '\'' +
                ", modelId='" + modelId + '\'' +
                '}';
    }
}
