package com.telegramflow.telegram;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

public class TelegramBot extends DefaultAbsSender {

    private String token;

    public TelegramBot(DefaultBotOptions options, String token) {
        super(options);
        this.token = token;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
