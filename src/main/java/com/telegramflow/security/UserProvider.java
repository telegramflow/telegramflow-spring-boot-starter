package com.telegramflow.security;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface UserProvider {

    @Nonnull
    User create(org.telegram.telegrambots.meta.api.objects.User telegramUser);

    @Nullable
    User get(org.telegram.telegrambots.meta.api.objects.User telegramUser);

    void save(User user);
}
