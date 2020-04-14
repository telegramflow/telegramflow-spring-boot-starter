package com.telegramflow.components;

public class Message {

    private String text;

    private Format format;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public enum Format {
        MARKDOWN,
        HTML
    }
}
