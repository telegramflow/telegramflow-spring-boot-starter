package com.telegramflow.security;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public interface Session {

    @Nonnull
    User getUser();

    void setUser(User user);

    @Nullable
    String getActiveScreen();

    void setActiveScreen(String activeScreen);

    @Nonnull
    AuthState getAuthState();

    void setAuthState(AuthState authState);

    @Nonnull
    Map<String, Object> getAttributes();

    void setAttributes(Map<String, Object> attributes);

}
