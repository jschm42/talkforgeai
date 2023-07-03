package com.talkforgeai.backend.persona.exception;

public class PersonaException extends RuntimeException {
    public PersonaException(String message) {
        super(message);
    }

    public PersonaException(String message, Throwable cause) {
        super(message, cause);
    }
}
