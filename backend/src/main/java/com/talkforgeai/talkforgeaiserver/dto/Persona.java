package com.talkforgeai.talkforgeaiserver.dto;


public record Persona(
        String name,
        String description,
        boolean withImagePromptSystem,
        String system,
        String personaImage,

        ChatGPTProperties chatGPTProperties,
        ElevenLabsProperties elevenLabsProperties) {

    public Persona(String name, String description) {
        this(name, description, true, "", "", null, null);
    }

    static final Persona DEFAULT_PERSONA = new Persona(
      "Default", "The default ChatGPT"
    );
}
