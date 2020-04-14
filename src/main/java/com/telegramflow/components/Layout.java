package com.telegramflow.components;

import com.telegramflow.screens.Screen;

import javax.annotation.Nonnull;

public class Layout {

    private Screen screen;

    private Message message = new Message();

    private Keyboard keyboard = new Keyboard();

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    @Nonnull
    public Message getMessage() {
        return message;
    }

    @Nonnull
    public Keyboard getKeyboard() {
        return keyboard;
    }

}
