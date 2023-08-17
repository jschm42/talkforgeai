package com.talkforgeai.service.openai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Date;
import java.util.List;


public record OpenAIChatStreamResponse(String id,
                                       String object,
                                       Date created,
                                       String model,
                                       List<StreamResponseChoice> choices,
                                       ResponseUsage usage) {

    public record StreamResponseChoice(Integer index,
                                       OpenAIChatMessage delta,
                                       @JsonProperty("finish_reason") FinishReason finishReason) {

        public enum FinishReason {
            STOP("stop"),
            FUNCTION_CALL("function_call");

            final String value;

            FinishReason(String value) {
                this.value = value;
            }

            @JsonValue
            public String getValue() {
                return value;
            }
        }

    }

    public record ResponseUsage(@JsonProperty("prompt_tokens") int promptTokens,
                                @JsonProperty("completion_tokens") int completionTokens,
                                @JsonProperty("total_tokens") int totalTokens) {
    }
}
