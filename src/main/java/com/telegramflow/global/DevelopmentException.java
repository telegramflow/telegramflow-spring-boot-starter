package com.telegramflow.global;

public class DevelopmentException extends RuntimeException {

    public DevelopmentException() {
    }

    public DevelopmentException(String message) {
        super(message);
    }

    public DevelopmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
