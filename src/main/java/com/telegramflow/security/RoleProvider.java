package com.telegramflow.security;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface RoleProvider {

    @Nullable
    Role get(User user);

    @Nonnull
    Role getNN(User user);
}
