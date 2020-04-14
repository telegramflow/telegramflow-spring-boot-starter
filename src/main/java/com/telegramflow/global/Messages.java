package com.telegramflow.global;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class Messages {

    private MessageSource messageSource;

    public Messages(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String key) {
        return messageSource.getMessage(key, null, key, LocaleContextHolder.getLocale());
    }

    public String formatMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, key, LocaleContextHolder.getLocale());
    }

    public String getMessage(Enum<?> caller) {
        String key = String.format("%s.%s", caller.getClass().getSimpleName(),  caller.name());
        return messageSource.getMessage(key, null, key, LocaleContextHolder.getLocale());
    }

}
