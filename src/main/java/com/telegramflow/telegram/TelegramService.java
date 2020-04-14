package com.telegramflow.telegram;

import com.telegramflow.components.*;
import com.telegramflow.security.Authentication;
import com.telegramflow.security.User;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service(TelegramService.NAME)
public class TelegramService {

    public static final String NAME = "tf$TelegramService";

    @Inject
    protected Authentication authentication;

    @Inject
    protected TelegramBot telegramBot;

    public void sendMessage(String text) {
        User user = authentication.getSessionNN().getUser();
        try {
            telegramBot.execute(new SendMessage()
                    .setChatId(String.valueOf(user.getId()))
                    .setText(text));
        } catch (TelegramApiException e) {
            throw new IllegalStateException("An error occurred while sending telegram message", e);
        }
    }

    public void sendLayout(Layout layout) {
        User user = authentication.getSessionNN().getUser();

        Message message = layout.getMessage();
        Keyboard keyboard = layout.getKeyboard();

        try {
            telegramBot.execute(new SendMessage()
                    .setChatId(String.valueOf(user.getId()))
                    .setText(message.getText())
                    .setParseMode(message.getFormat() != null ? message.getFormat().name() : null)
                    .setReplyMarkup(createKeyboard(keyboard)));
        } catch (TelegramApiException e) {
            throw new IllegalStateException("An error occurred while sending telegram message", e);
        }
    }

    protected ReplyKeyboard createKeyboard(Keyboard keyboard) {
        if (keyboard.getRemoveKeyboard() == Boolean.TRUE) {
            return new ReplyKeyboardRemove();
        }

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setOneTimeKeyboard(keyboard.getOneTimeKeyboard());
        keyboardMarkup.setResizeKeyboard(keyboard.getResizeKeyboard());

        List<ButtonRow> buttonRows = keyboard.getButtonRows();
        if (buttonRows != null) {
            List<KeyboardRow> keyboardRows = new ArrayList<>();
            if (keyboard.getColumns() == null) {
                for (ButtonRow buttonRow : buttonRows) {
                    KeyboardRow keyboardRow = new KeyboardRow();
                    for (Button button : buttonRow.getButtons()) {
                        keyboardRow.add(createKeyboardButton(button));
                    }
                    keyboardRows.add(keyboardRow);
                }
            } else {
                KeyboardRow keyboardRow = null;
                List<Button> visibleButtons = getVisibleButtons(keyboard);
                for (int i = 0; i < visibleButtons.size(); i++) {
                    if (i % keyboard.getColumns() == 0) {
                        keyboardRow = new KeyboardRow();
                        keyboardRows.add(keyboardRow);
                    }
                    Button button = visibleButtons.get(i);
                    keyboardRow.add(createKeyboardButton(button));
                }
            }
            keyboardMarkup.setKeyboard(keyboardRows);
        }

        return keyboardMarkup;
    }

    private List<Button> getVisibleButtons(Keyboard keyboard) {
        List<Button> visibleButtons = new ArrayList<>();
        List<ButtonRow> buttonRows = keyboard.getButtonRows();
        if (buttonRows != null) {
            for(ButtonRow buttonRow : buttonRows) {
                for(Button button : buttonRow.getButtons()) {
                    if (button.isVisible()) {
                        visibleButtons.add(button);
                    }
                }
            }
        }
        return visibleButtons;
    }

    private KeyboardButton createKeyboardButton(Button button) {
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText(button.getCaption());
        keyboardButton.setRequestContact(button.isRequestContact());
        keyboardButton.setRequestLocation(button.isRequestLocation());
        return keyboardButton;
    }
}
