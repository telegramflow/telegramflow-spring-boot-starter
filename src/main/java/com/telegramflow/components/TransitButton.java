package com.telegramflow.components;

import com.telegramflow.global.BeanLocator;
import com.telegramflow.screens.Screen;
import com.telegramflow.telegram.FlowService;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TransitButton extends AbstractButton {

    private String transitTo;

    public TransitButton(Layout layout, String caption, String transitTo) {
        super(layout, null, caption);
        this.transitTo = transitTo;
    }

    public TransitButton(Layout layout, String id, String caption, String transitTo) {
        super(layout, id, caption);
        this.transitTo = transitTo;
    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public void execute(Update update) {
        Screen screen = layout.getScreen();
        BeanLocator beanLocator = screen.getBeanLocator();
        FlowService flowService = beanLocator.get(FlowService.NAME);
        flowService.transitTo(transitTo);
    }
}
