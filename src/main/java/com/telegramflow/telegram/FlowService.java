package com.telegramflow.telegram;

import com.telegramflow.components.Button;
import com.telegramflow.components.ButtonRow;
import com.telegramflow.components.Keyboard;
import com.telegramflow.screens.*;
import com.telegramflow.security.Authentication;
import com.telegramflow.security.AuthenticationException;
import com.telegramflow.security.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Inject;
import java.util.*;

@Service(FlowService.NAME)
public class FlowService {

    public static final String NAME = "tf$FlowService";

    private Logger logger = LoggerFactory.getLogger(FlowService.class);

    private final static String START_COMMAND = "/start";

    @Inject
    protected Authentication authentication;

    @Inject
    protected ScreenSelector screenSelector;

    @Inject
    private ScreenConfig screenConfig;

    @Inject
    private ScreenManager screenManager;

    private Set<UpdateHandler> updateHandlers = new HashSet<>();

    public void addUpdateHandler(UpdateHandler updateHandler) {
        updateHandlers.add(updateHandler);
    }

    public void removeUpdateHandler(UpdateHandler updateHandler) {
        updateHandlers.remove(updateHandler);
    }

    public void process(Update update) throws AuthenticationException {
        Objects.requireNonNull(update, "update is null");

        logger.info("Process update {}", update.getUpdateId());

        Session session = authentication.authorize(update);
        try {
            String text = TelegramUtil.extractText(update);
            if (START_COMMAND.equals(text)) {
                session.setActiveScreen(null);
            }
            if (session.getActiveScreen() != null) {
                ScreenInfo screenInfo = screenConfig.getScreenInfo(session.getActiveScreen());
                process(screenInfo, update);
            } else {
                String initialScreen = screenSelector.get(session.getUser());
                ScreenInfo screenInfo = screenConfig.getScreenInfo(initialScreen);
                transitTo(screenInfo);
            }
        } finally {
            authentication.end();
        }
    }

    protected void process(ScreenInfo screenInfo, Update update) {
        for(UpdateHandler updateHandler : updateHandlers) {
            logger.info("Executing update handler {}", updateHandler);
            if (updateHandler.handle(update)) {
                return;
            }
        }

        Screen screen = screenManager.create(screenInfo);
        Map<String, Button> availableButtons  = getAvailableButtons(screen);
        if (update.hasMessage() && update.getMessage().hasText()
                && availableButtons.containsKey(update.getMessage().getText())) {
            Button button = availableButtons.get(update.getMessage().getText());
            logger.info("Executing button '{}' on screen '{}'",
                    button.getCaption(), screen.getId());
            button.execute(update);
        } else {
            logger.info("Handle input on screen '{}'", screen.getId());
            screen.handleInput(update);
        }
    }

    protected Map<String, Button> getAvailableButtons(Screen screen) {
        Map<String, Button> availableButtons = new HashMap<>();
        Keyboard keyboard = screen.getLayout().getKeyboard();

        if (keyboard.getButtonRows() != null) {
            for (ButtonRow buttonRow : keyboard.getButtonRows()) {
                List<Button> buttons = buttonRow.getButtons();
                buttons.forEach(button ->
                        availableButtons.put(button.getCaption(), button));
            }
        }

        return availableButtons;
    }

    public void transitTo(String screenId) {
        Objects.requireNonNull(screenId, "screenId is null");

        ScreenInfo screenInfo = screenConfig.getScreenInfo(screenId);
        transitTo(screenInfo);
    }

    public void transitTo(ScreenInfo screenInfo) {
        Objects.requireNonNull(screenInfo, "screenInfo is null");

        Session session = authentication.getSessionNN();

        logger.info("Transiting user {} to screen '{}'",
                session.getUser().getId(), screenInfo.getId());

        Screen screen = screenManager.create(screenInfo);

        screen.beforeShow();

        screen.show();
        session.setActiveScreen(screenInfo.getId());

        screen.afterShow();
    }

}
