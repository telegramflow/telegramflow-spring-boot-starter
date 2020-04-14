package com.telegramflow.global;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component(BeanLocator.NAME)
public class BeanLocator implements ApplicationContextAware {

    public static final String NAME = "tf$BeanLocator";

    private static Map<Class, Optional<String>> names = new ConcurrentHashMap<>();

    private ApplicationContext applicationContext;

    public <T> T get(Class<T> beanType) {
        Objects.requireNonNull(beanType, "beanType is null");
        String name = findName(beanType);
        // If the name is found, look up the bean by name because it is much faster
        if (name == null)
            return applicationContext.getBean(beanType);
        else
            return applicationContext.getBean(name, beanType);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        Objects.requireNonNull(name, "name is null");
        return (T) applicationContext.getBean(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name, @Nullable Class<T> beanType) {
        Objects.requireNonNull(name, "name is null");
        if (beanType != null) {
            return applicationContext.getBean(name, beanType);
        }
        return (T) applicationContext.getBean(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T getPrototype(String name, Object... args) {
        Objects.requireNonNull(name, "name is null");
        return (T) applicationContext.getBean(name, args);
    }

    @SuppressWarnings("unchecked")
    public <T> T getPrototype(Class<T> beanType, Object... args) {
        Objects.requireNonNull(beanType, "beanType is null");
        String name = findName(beanType);
        // If the name is found, look up the bean by name
        if (name == null)
            return applicationContext.getBean(beanType);
        else
            return (T) applicationContext.getBean(name, args);
    }

    public <T> Map<String, T> getAll(Class<T> beanType) {
        return applicationContext.getBeansOfType(beanType);
    }

    public boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Nullable
    private <T> String findName(Class<T> beanType) {
        String name = null;
        Optional<String> optName = names.get(beanType);
        //noinspection OptionalAssignedToNull
        if (optName == null) {
            // Try to find a bean name defined in its NAME static field
            try {
                Field nameField = beanType.getField("NAME");
                name = (String) nameField.get(null);
            } catch (NoSuchFieldException | IllegalAccessException ignore) {
            }
            names.put(beanType, Optional.ofNullable(name));
        } else {
            name = optName.orElse(null);
        }
        return name;
    }
}
