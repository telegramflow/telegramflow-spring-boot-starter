package com.telegramflow.screens;

import com.telegramflow.components.Button;
import com.telegramflow.components.ButtonRow;
import com.telegramflow.components.Component;
import com.telegramflow.components.Layout;
import com.telegramflow.global.BeanLocator;
import com.telegramflow.telegram.TelegramService;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Screen {

    private String id;

    private Layout layout;

    private BeanLocator beanLocator;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        layout.setScreen(this);
        this.layout = layout;
    }

    public BeanLocator getBeanLocator() {
        return beanLocator;
    }

    @Inject
    public void setBeanLocator(BeanLocator beanLocator) {
        this.beanLocator = beanLocator;
    }

    public Set<Component> getComponents() {
        Set<Component> components = new HashSet<>();
        List<ButtonRow> buttonRows = layout.getKeyboard().getButtonRows();
        if (buttonRows != null) {
            for(ButtonRow buttonRow : buttonRows) {
                components.addAll(buttonRow.getButtons());
            }
        }
        return components;
    }

    public Component getComponent(String id) {
        List<ButtonRow> buttonRows = layout.getKeyboard().getButtonRows();
        if (buttonRows != null) {
            for(ButtonRow buttonRow : buttonRows) {
                for(Button button : buttonRow.getButtons()) {
                    if (id.equals(button.getId())) {
                        return button;
                    }
                }
            }
        }
        return null;
    }

    public Component getComponentNN(String id) {
        Component component = getComponent(id);
        if (component == null) {
            throw new IllegalArgumentException(String.format("Cannot find component with id '%s'", id));
        }
        return component;
    }

    public void init() {
    }

    public void beforeShow() {

    }

    public void show() {
        TelegramService telegramService = beanLocator.get(TelegramService.NAME);
        telegramService.sendLayout(layout);
    }

    public void afterShow() {

    }

    public void handleInput(Update update) {
    }
}
