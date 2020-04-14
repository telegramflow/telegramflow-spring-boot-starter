package com.telegramflow.screens;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ScreenConfig {

    public static final String NAME = "tf$ScreenConfig";

    private Logger logger = LoggerFactory.getLogger(ScreenConfig.class);

    protected ScreenScanner screenScanner;

    protected Map<String, ScreenInfo> screens = new HashMap<>();

    protected ReadWriteLock lock = new ReentrantReadWriteLock();

    public void setScreenScanner(ScreenScanner screenScanner) {
        this.screenScanner = screenScanner;
    }

    public void initialize() throws ClassNotFoundException {
        long startTime = System.currentTimeMillis();

        screens.clear();

        screens.putAll(scan());

        logger.info("ScreenConfig initialized in {} ms", System.currentTimeMillis() - startTime);
    }

    public ScreenInfo getScreenInfo(String id) {
        lock.readLock().lock();
        try {
            ScreenInfo windowInfo = screens.get(id);
            if (windowInfo == null) {
                throw new NoSuchScreenException(id);
            }
            return windowInfo;
        } finally {
            lock.readLock().unlock();
        }
    }

    protected Map<String, ScreenInfo> scan() throws ClassNotFoundException {
        Set<Class<?>> screenClasses = screenScanner.scan(UiController.class);

        Map<String, ScreenInfo> projectScreens = new HashMap<>();

        for(Class<?> screenClass : screenClasses) {
            String screenId = UiDescriptorUtils.getInferredScreenId(screenClass);
            if (!Screen.class.isAssignableFrom(screenClass)) {
                throw new RuntimeException(
                        String.format("Screen %s must be extended from Screen class",
                                screenId));
            }
            ScreenInfo existingScreen = projectScreens.get(screenId);
            if (existingScreen != null) {
                throw new RuntimeException(
                        String.format("Project contains screens with the same id: '%s'. See '%s' and '%s'",
                                screenId,
                                screenClass.getName(),
                                existingScreen.getScreenClass().getName()));
            } else {
                String templatePath = getTemplatePath(screenClass);
                ScreenInfo screenInfo = new ScreenInfo(screenId, screenClass, templatePath);
                projectScreens.put(screenId, screenInfo);
            }
        }

        return projectScreens;
    }

    public static String getTemplatePath(Class<?> screenClass) {
        String templatePath = UiDescriptorUtils.getInferredTemplatePath(screenClass);
        if (templatePath != null && !templatePath.startsWith("/")) {
            String packageName = UiDescriptorUtils.getPackage(screenClass);
            if (StringUtils.isNotEmpty(packageName)) {
                String relativePath = packageName.replace('.', '/');
                templatePath = "/" + relativePath + "/" + templatePath;
            }
        }
        return templatePath;

    }
}
