package com.telegramflow.telegram;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateHandler {

    boolean handle(Update update);
}
