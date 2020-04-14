package com.telegramflow.screens;

public class NoSuchScreenException extends RuntimeException {

    private static final long serialVersionUID = -3751833162235475862L;

    private final String screenId;

    public NoSuchScreenException(String screenId) {
        super(String.format("Screen '%s' is not defined. " +
                "Make sure the screen controller is located inside or below the base package", screenId));

        this.screenId = screenId;
    }

    public String getScreenId() {
        return screenId;
    }
}
