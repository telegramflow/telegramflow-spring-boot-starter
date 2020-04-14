package com.telegramflow.telegram;

import com.telegramflow.global.Messages;
import com.telegramflow.security.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

public class DefaultWebhookBot extends TelegramWebhookBot implements UpdateReceiver {

    private Logger logger = LoggerFactory.getLogger(DefaultWebhookBot.class);

    @Inject
    private ThreadPoolTaskExecutor threadExecutor;

    @Inject
    private FlowService flowProcessor;

    @Inject
    private Messages messages;

    private String token;

    private String username;

    public DefaultWebhookBot(DefaultBotOptions options, String token, String username) {
        super(options);
        this.token = token;
        this.username = username;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotPath() {
        return username;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        handle(update);
        return null;
    }

    @Override
    public void handle(Update update) {
        CompletableFuture.runAsync(() -> {
            process(update);
        }, threadExecutor);
    }

    protected void process(Update update) {
        try {
            flowProcessor.process(update);
        } catch (AuthenticationException e) {
            logger.error("Authentication failed", e);
        } catch (Exception e) {
            logger.error(String.format("An error occurred while processing update %d",
                    update.getUpdateId()), e);
            User telegramUser = TelegramUtil.extractUser(update);
            if (telegramUser != null) {
                sendMessage(telegramUser.getId(), messages.getMessage("process.unexpectedError"));
            }

        }
    }

    public void sendMessage(Integer userId, String text) {
        try {
            execute(new SendMessage().setChatId((long)userId).setText(text));
        } catch (TelegramApiException e) {
            logger.error("An error occurred while sending message to telegram");
        }
    }

}
