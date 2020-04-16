package com.telegramflow.components;

import java.util.ArrayList;
import java.util.List;

public class Keyboard {

    private Boolean removeKeyboard;

    private Boolean oneTimeKeyboard;

    private Boolean resizeKeyboard = true;

    private Integer columns;

    private List<ButtonRow> buttonRows;

    public Boolean getRemoveKeyboard() {
        return removeKeyboard;
    }

    public void setRemoveKeyboard(Boolean removeKeyboard) {
        this.removeKeyboard = removeKeyboard;
    }

    public Boolean getOneTimeKeyboard() {
        return oneTimeKeyboard;
    }

    public void setOneTimeKeyboard(Boolean oneTimeKeyboard) {
        this.oneTimeKeyboard = oneTimeKeyboard;
    }

    public Boolean getResizeKeyboard() {
        return resizeKeyboard;
    }

    public void setResizeKeyboard(Boolean resizeKeyboard) {
        this.resizeKeyboard = resizeKeyboard;
    }

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public List<ButtonRow> getButtonRows() {
        return buttonRows;
    }

    public void setButtonRows(List<ButtonRow> buttonRows) {
        this.buttonRows = buttonRows;
    }

    public void addButtonRow(ButtonRow buttonRow) {
        if (buttonRows == null) {
            buttonRows = new ArrayList<>();
        }
        buttonRows.add(buttonRow);
    }

    public void addButton(Button button) {
        if (buttonRows == null) {
            buttonRows = new ArrayList<>();
        }
        buttonRows.add(new ButtonRow(button));
    }
}
