package com.talkforgeai.talkforgeaiserver.openai.dto;

import java.util.List;

public record OpenAIFunctions(String name, String description, Parameters parameters) {
    public static record Parameters(String type,
                                    Properties properties,
                                    List<String> required) {
        public static record Properties(
                String imagePrompt) {
            public Properties(String imagePrompt) {
                this.imagePrompt = imagePrompt;
            }
        }
    }

}
