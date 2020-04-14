package com.telegramflow.screens;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class UiDescriptorUtils {

    public static String getInferredScreenId(Class<?> screenClass) {
        Objects.requireNonNull(screenClass, "screenClass is null");
        UiController uiController = screenClass.getAnnotation(UiController.class);
        if (uiController == null) {
            throw new RuntimeException("Screen class must be annotated with @UiController: " + screenClass);
        }
        return !StringUtils.isEmpty(uiController.value())
                ? uiController.value()
                : uiController.id();
    }

    public static String getInferredTemplatePath(Class<?> screenClass) {
        Objects.requireNonNull(screenClass, "screenClass is null");
        UiDescriptor uiDescriptor = screenClass.getAnnotation(UiDescriptor.class);
        if (uiDescriptor == null) {
            return null;
        }
        String templateLocation = uiDescriptor.value();
        if (Strings.isNullOrEmpty(templateLocation)) {
            templateLocation = uiDescriptor.path();

            if (Strings.isNullOrEmpty(templateLocation)) {
                throw new RuntimeException("Screen class annotated with @UiDescriptor without template: " + screenClass);
            }
        }

        return templateLocation;
    }

    public static String getPackage(Class controllerClass) {
        Package javaPackage = controllerClass.getPackage();
        if (javaPackage != null) {
            return javaPackage.getName();
        }

        // infer from FQN, hot-deployed classes do not have package
        // see JDK-8189231

        String canonicalName = controllerClass.getCanonicalName();
        int dotIndex = canonicalName.lastIndexOf('.');

        if (dotIndex >= 0) {
            return canonicalName.substring(0, dotIndex);
        }

        return "";
    }

}
