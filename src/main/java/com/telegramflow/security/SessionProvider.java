package com.telegramflow.security;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SessionProvider {

    @Nonnull
    Session create(User user);

    @Nullable
    Session get(User user);

    void save(Session session);
}
