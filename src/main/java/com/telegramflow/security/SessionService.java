package com.telegramflow.security;

import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Map;

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
}
