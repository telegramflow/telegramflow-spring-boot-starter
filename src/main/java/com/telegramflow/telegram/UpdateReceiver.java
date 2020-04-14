package com.telegramflow.telegram;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateReceiver {

    void handle(Update update);
}
