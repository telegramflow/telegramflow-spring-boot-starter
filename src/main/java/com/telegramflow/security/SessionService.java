package com.telegramflow.security;

import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Map;
import java.util.function.Consumer;

@Service(SessionService.NAME)
public class SessionService {

    public static final String NAME = "tf$SessionService";

    @Inject
    private Authentication authentication;

    @Nonnull
    public Session getSession() {
        return authentication.getSessionNN();
    }

    @Nonnull
    public Map<String, Object> getAttributes() {
        return authentication.getSessionNN().getAttributes();
    }

    public void withUser(Consumer<User> consumer) {
        Session session = getSession();
        consumer.accept(session.getUser());
    }
}
