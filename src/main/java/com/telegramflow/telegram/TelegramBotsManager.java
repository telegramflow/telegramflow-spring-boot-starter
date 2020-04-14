package com.telegramflow.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.meta.generics.WebhookBot;

public class TelegramBotsManager {

    private Logger logger = LoggerFactory.getLogger(TelegramBotsManager.class);

    protected TelegramBotsApi telegramBotsApi;

    public TelegramBotsManager(TelegramBotsApi telegramBotsApi) {
        this.telegramBotsApi = telegramBotsApi;
    }

    public void register(UpdateReceiver updateReceiver) throws TelegramApiRequestException {
        if (updateReceiver instanceof WebhookBot) {
            WebhookBot webhookBot = (WebhookBot) updateReceiver;
            telegramBotsApi.registerBot(webhookBot);
            logger.info("Registered webhook bot '{}'", webhookBot.getBotUsername());
        } else if (updateReceiver instanceof LongPollingBot) {
            LongPollingBot longPollingBot = (LongPollingBot) updateReceiver;
            telegramBotsApi.registerBot(longPollingBot);
            logger.info("Registered long-polling bot '{}'", longPollingBot.getBotUsername());
        } else {
            throw new IllegalStateException("UpdateReceiver should be implemented by WebhookBot or LongPollingBot");
        }
    }
}
