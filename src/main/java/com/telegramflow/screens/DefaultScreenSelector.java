package com.telegramflow.screens;

import com.telegramflow.security.User;

public class DefaultScreenSelector implements ScreenSelector {

    private final static String INITIAL_SCREEN = "index";

    @Override
    public String get(User user) {
        return INITIAL_SCREEN;
    }
}
