package com.telegramflow.components;

import com.telegramflow.screens.Screen;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;

public class InvokeButton extends AbstractButton {

    protected String methodName;

    public InvokeButton(Layout layout, String id, String caption, String methodName) {
        super(layout, id, caption);
        this.methodName = methodName;
    }

    @Override
    public void execute(Update update) {
        if (StringUtils.isEmpty(methodName)) {
            return;
        }

        Screen screen = layout.getScreen();

        Method method;
        try {
            method = screen.getClass().getMethod(methodName, Update.class);
        } catch (NoSuchMethodException e) {
            try {
                method = screen.getClass().getMethod(methodName);
            } catch (NoSuchMethodException e1) {
                throw new IllegalStateException(String.format("No suitable methods named %s for button %s", methodName, id));
            }
        }

        try {
            if (method.getParameterCount() == 1) {
                method.invoke(screen, update);
            } else {
                method.invoke(screen);
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception on button invoking", e);
        }
    }
}
