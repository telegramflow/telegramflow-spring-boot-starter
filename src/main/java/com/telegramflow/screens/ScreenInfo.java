package com.telegramflow.screens;

public class ScreenInfo {

    private String id;

    private Class<?> screenClass;

    private String templatePath;

    public ScreenInfo(String id, Class<?> screenClass, String templatePath) {
        this.id = id;
        this.screenClass = screenClass;
        this.templatePath = templatePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Class<?> getScreenClass() {
        return screenClass;
    }

    public void setScreenClass(Class<?> screenClass) {
        this.screenClass = screenClass;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }
}
