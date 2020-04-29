package com.telegramflow.security;

import com.telegramflow.global.Messages;
import com.telegramflow.telegram.TelegramBot;
import com.telegramflow.telegram.TelegramUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;

public class SharePhoneAuthentication implements Authentication {

    private Logger logger = LoggerFactory.getLogger(SharePhoneAuthentication.class);

    protected final static ThreadLocal<Session> SESSION_HOLDER = new ThreadLocal<>();

    protected UserProvider userProvider;

    protected RoleProvider roleProvider;

    protected SessionProvider sessionProvider;

    protected TelegramBot telegramBot;

    protected Messages messages;

    protected Consumer<User> afterAuthorized = (user) -> {
        try {
            telegramBot.execute(new SendMessage()
                    .setChatId((long)user.getId())
                    .setText(messages.getMessage("authentication.authorizedMessage"))
                    .setReplyMarkup(new ReplyKeyboardRemove()));
        } catch (TelegramApiException e) {
            logger.error(String.format("An error occurred while sending authorized message to user %s",
                    user.getUsername()), e);
        }
    };

    protected Consumer<User> afterRestricted = (user) -> {
        try {
            telegramBot.execute(new SendMessage()
                    .setChatId((long)user.getId())
                    .setText(messages.getMessage("authentication.restrictedMessage"))
                    .setReplyMarkup(new ReplyKeyboardRemove()));
        } catch (TelegramApiException e) {
            logger.error(String.format("An error occurred while sending restricted message to user %s",
                    user.getUsername()), e);
        }
    };

    public SharePhoneAuthentication(UserProvider userProvider, RoleProvider roleProvider,
                                    SessionProvider sessionProvider, TelegramBot telegramBot, Messages messages) {
        this.userProvider = userProvider;
        this.roleProvider = roleProvider;
        this.sessionProvider = sessionProvider;
        this.telegramBot = telegramBot;
        this.messages = messages;
    }

    @Nonnull
    public void setAfterAuthorized(@Nullable Consumer<User> afterAuthorized) {
        this.afterAuthorized = afterAuthorized;
    }

    @Nonnull
    public void setAfterRestricted(@Nullable Consumer<User> afterRestricted) {
        this.afterRestricted = afterRestricted;
    }

    @Nonnull
    @Override
    public Session authorize(@Nonnull Update update) throws AuthenticationException {
        Objects.requireNonNull(update, "update is null");

        org.telegram.telegrambots.meta.api.objects.User telegramUser = TelegramUtil.extractUser(update);

        User user = userProvider.get(telegramUser);
        if (user == null) {
            user = userProvider.create(telegramUser);
            userProvider.save(user);
        }

        Session session = sessionProvider.get(user);
        if (session == null) {
            session = sessionProvider.create(user);
            sessionProvider.save(session);
        }

        if (session.getAuthState() == AuthState.UNAUTHORIZED) {
            session.setAuthState(AuthState.AUTHORIZATION);
            sessionProvider.save(session);
            sendAuthorizationRequest(user);
            throw new AuthenticationException(String.format("Authorization process required for user %s",
                    user.getUsername()));
        }

        if (session.getAuthState() == AuthState.AUTHORIZATION || user.getRole() == null) {
            Contact contact = TelegramUtil.extractContact(update);

            if (contact == null) {
                sendAuthorizationRequest(user);
                throw new AuthenticationException(String.format("User %s sent invalid authorization message",
                        user.getUsername()));
            }

            if (!user.getId().equals(contact.getUserID())) {
                sendAuthorizationRequest(user);
                throw new AuthenticationException(String.format("Contact %s doesn't belong to user %s",
                        contact.getPhoneNumber(), user.getUsername()));
            }

            String normalizedPhone = TelegramUtil.normalizePhone(contact.getPhoneNumber());
            user.setPhone(normalizedPhone);

            matchRole(user);

            session.setUser(user);
            session.setAuthState(AuthState.AUTHORIZED);
            sessionProvider.save(session);

            if (afterAuthorized != null) {
                afterAuthorized.accept(user);
            }
        }

        if (user.getRole() == null) {
            sendAuthorizationRequest(user);
            throw new AuthenticationException(String.format("User %s doesn't have role", user.getUsername()));
        }

        if (session.getAuthState() != AuthState.AUTHORIZED) {
            throw new AuthenticationException(String.format("User %s has not authorized", user.getUsername()));
        }

        SESSION_HOLDER.set(session);

        logger.info("User {} successfully authorized with role {}", user.getUsername(), user.getRole());

        return session;
    }

    protected void matchRole(User user) throws AuthenticationException {
        Objects.requireNonNull(user, "user is null");

        Role role = roleProvider.get(user);

        if (role == null) {
            if (afterRestricted != null) {
                afterRestricted.accept(user);
            }
            throw new AuthenticationException(String.format("Phone %s is not matched with any role for user %s",
                    user.getPhone(), user.getUsername()));
        }

        user.setRole(role);
        userProvider.save(user);

        logger.info("Role {} matched and assigned to user {}", role, user.getUsername());
    }

    protected void sendAuthorizationRequest(User user) {
        Objects.requireNonNull(user, "user is null");

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton()
                .setRequestContact(true)
                .setText(messages.getMessage("authentication.authorizeButton")));
        try {
            telegramBot.execute(new SendMessage()
                    .setChatId((long)user.getId())
                    .setText(messages.getMessage("authentication.authorizeMessage"))
                    .setReplyMarkup(new ReplyKeyboardMarkup()
                            .setResizeKeyboard(true)
                            .setKeyboard(Collections.singletonList(keyboardRow))));
        } catch (TelegramApiException e) {
            logger.error(String.format("An error occurred while sending authorization request to user %s",
                    user.getUsername()), e);
        }
    }

    @Nullable
    @Override
    public Session getSession() {
        return SESSION_HOLDER.get();
    }

    @Nonnull
    @Override
    public Session getSessionNN() {
        Session session = getSession();
        if (session == null) {
            throw new IllegalStateException("There is no active session");
        }
        return session;
    }

    @Override
    public void end() {
        Session session = getSessionNN();
        sessionProvider.save(session);
        SESSION_HOLDER.remove();
    }

    @Override
    public void logout() {
        Session session = getSessionNN();

        User user = session.getUser();
        user.setRole(null);
        userProvider.save(user);

        session.setActiveScreen(null);
        session.setAuthState(null);
        session.setAttributes(null);
        sessionProvider.save(session);

        end();

        logger.info("User {} logged out", user.getUsername());
    }

}
