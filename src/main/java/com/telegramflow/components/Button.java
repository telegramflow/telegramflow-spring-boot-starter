package com.telegramflow.components;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Button extends Component {

    String getCaption();

    boolean isRequestContact();

    boolean isRequestLocation();

    void execute(Update update);
}
