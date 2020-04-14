package com.telegramflow.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ButtonRow extends ArrayList<Button> {

    public ButtonRow(Button... buttons) {
        super(Arrays.asList(buttons));
    }

    public List<Button> getButtons() {
        return this;
    }
}
