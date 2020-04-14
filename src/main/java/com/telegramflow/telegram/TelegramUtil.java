package com.telegramflow.telegram;

import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class TelegramUtil {

    @Nullable
    public static User extractUser(@Nonnull Update update) {
        Objects.requireNonNull(update, "update is null");

        if (update.hasMessage()) {
            return update.getMessage().getFrom();
        } else if (update.hasEditedMessage()) {
            return update.getEditedMessage().getFrom();
        } else if (update.hasInlineQuery()) {
            return update.getInlineQuery().getFrom();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom();
        } else if (update.hasChannelPost()) {
            return update.getChannelPost().getFrom();
        } else if (update.hasEditedChannelPost()) {
            return update.getEditedChannelPost().getFrom();
        } else if (update.hasChosenInlineQuery()) {
            return update.getChosenInlineQuery().getFrom();
        } else if (update.hasPreCheckoutQuery()) {
            return update.getPreCheckoutQuery().getFrom();
        } else if (update.hasShippingQuery()) {
            return update.getShippingQuery().getFrom();
        } else if (update.hasPollAnswer()) {
            return update.getPollAnswer().getUser();
        }
        return null;
    }

    @Nullable
    public static Contact extractContact(@Nonnull Update update) {
        Objects.requireNonNull(update, "update is null");

        if (update.hasMessage() && update.getMessage().hasContact()) {
            return update.getMessage().getContact();
        }
        return null;
    }

    @Nullable
    public static String extractText(@Nonnull Update update) {
        Objects.requireNonNull(update, "update is null");

        if (update.hasMessage() && update.getMessage().hasText()) {
            return update.getMessage().getText();
        }
        return null;
    }

    @Nonnull
    public static String normalizePhone(@Nonnull String phone) {
        Objects.requireNonNull(phone, "phone is null");
        return phone.replaceAll("\\+|\\s", "");
    }

}
