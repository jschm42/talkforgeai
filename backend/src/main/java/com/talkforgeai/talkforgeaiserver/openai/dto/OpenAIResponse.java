package com.talkforgeai.talkforgeaiserver.openai.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Date;
import java.util.List;


public record OpenAIResponse(String id,
                             String object,
                             Date created,
                             String model,
                             List<ResponseChoice> choices,
                             ResponseUsage usage) {

    public record ResponseChoice(Integer index,
                                 @JsonAlias({"delta"}) OpenAIChatMessage message,
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
