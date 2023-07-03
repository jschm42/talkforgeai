package com.talkforgeai.service.openai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.Map;

public record OpenAIFunction(String name,
                             String description,
                             FunctionParameters parameters,
                             @JsonProperty("function_call") FunctionCall functionCall) {

    public enum FunctionCall {
        AUTO("auto"),
        NONE("none");

        private final String value;

        FunctionCall(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

    }

    public record FunctionParameters(ParameterType type,
                                     Map<String, FunctionProperty> properties,
                                     List<String> required) {

        public enum ParameterType {
            OBJECT("object"),
            STRING("string");

            private String value;

            ParameterType(String value) {
                this.value = value;
            }

            @JsonValue
            public String getValue() {
                return value;
            }
        }

        public record FunctionProperty(ParameterType type,
                                       String description) {

        }


    }

}
