package com.telegramflow.security;

import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Authentication {

    @Nonnull
    Session authorize(@Nonnull Update update) throws AuthenticationException;

    @Nullable
    Session getSession();

    @Nonnull
    Session getSessionNN();

    void end();

    void logout();
}
