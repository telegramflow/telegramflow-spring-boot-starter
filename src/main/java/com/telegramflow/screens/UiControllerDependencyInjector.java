package com.telegramflow.screens;

import com.telegramflow.components.Keyboard;
import com.telegramflow.components.Message;
import com.telegramflow.global.BeanLocator;
import com.telegramflow.screens.UiControllerReflectionInspector.InjectElement;
import com.telegramflow.screens.UiControllerReflectionInspector.ScreenMetadata;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.*;
import java.util.List;
import java.util.Map;

@Component(UiControllerDependencyInjector.NAME)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UiControllerDependencyInjector {

    public static final String NAME = "tf$UiControllerDependencyInjector";

    private Logger logger = LoggerFactory.getLogger(UiControllerDependencyInjector.class);

    protected BeanLocator beanLocator;
    protected UiControllerReflectionInspector reflectionInspector;

    @Inject
    public void setBeanLocator(BeanLocator beanLocator) {
        this.beanLocator = beanLocator;
    }

    @Inject
    public void setReflectionInspector(UiControllerReflectionInspector reflectionInspector) {
        this.reflectionInspector = reflectionInspector;
    }

    public <T extends Screen> void inject(T screen) {
        ScreenMetadata screenMetadata = reflectionInspector.getScreenIntrospectionData(screen.getClass());
        injectValues(screen, screenMetadata);
    }

    protected <T extends Screen> void injectValues(T screen, ScreenMetadata screenMetadata) {
        List<InjectElement> injectElements = screenMetadata.getInjectElements();

        for (InjectElement entry : injectElements) {
            doInjection(entry.getElement(), entry.getAnnotationClass(), screen);
        }
    }

    protected <T extends Screen> void doInjection(AnnotatedElement element, Class annotationClass, T screen) {
        Class<?> type;
        String name = null;
        if (annotationClass == Named.class) {
            name = element.getAnnotation(Named.class).value();
        } else if (annotationClass == Resource.class) {
            name = element.getAnnotation(Resource.class).name();
        }

        boolean required = true;
        if (element.isAnnotationPresent(Autowired.class)) {
            required = element.getAnnotation(Autowired.class).required();
        }

        if (element instanceof Field) {
            type = ((Field) element).getType();
            if (StringUtils.isEmpty(name)) {
                name = ((Field) element).getName();
            }
        } else if (element instanceof Method) {
            Class<?>[] types = ((Method) element).getParameterTypes();
            if (types.length != 1) {
                throw new IllegalStateException("Can inject to methods with one parameter only");
            }

            type = types[0];
            if (StringUtils.isEmpty(name)) {
                if (((Method) element).getName().startsWith("set")) {
                    name = StringUtils.uncapitalize(((Method) element).getName().substring(3));
                } else {
                    name = ((Method) element).getName();
                }
            }
        } else {
            throw new IllegalStateException("Can inject to fields and setter methods only");
        }

        Object instance = getInjectedInstance(type, name, annotationClass, element, screen);

        if (instance != null) {
            assignValue(element, instance, screen);
        } else if (required) {
            Class<?> declaringClass = ((Member) element).getDeclaringClass();
            Class<? extends Screen> frameClass = screen.getClass();

            String msg;
            if (frameClass == declaringClass) {
                msg = String.format(
                        "Unable to find an instance of type '%s' named '%s' for instance of '%s'",
                        type, name, frameClass.getCanonicalName());
            } else {
                msg = String.format(
                        "Unable to find an instance of type '%s' named '%s' declared in '%s' for instance of '%s'",
                        type, name, declaringClass.getCanonicalName(), frameClass.getCanonicalName());
            }

            logger.warn(msg);
        } else {
            logger.trace("Skip injection {} of {} as it is optional and instance not found",
                    name, screen.getClass());
        }
    }

    @SuppressWarnings("unused")
    @Nullable
    protected <T extends Screen> Object getInjectedInstance(Class<?> type, String name, Class annotationClass, AnnotatedElement element,
                                         T screen) {
        if (Message.class.isAssignableFrom(type)) {
            // Injecting message component
            return screen.getLayout().getMessage();
        } else if (Keyboard.class.isAssignableFrom(type)) {
            // Injecting keyboard component
            return screen.getLayout().getKeyboard();
        } else if (com.telegramflow.components.Component.class.isAssignableFrom(type)) {
            // Injecting a UI component
            return screen.getComponent(name);
        } else {
            Object instance;
            // Try to find a Spring bean
            Map<String, ?> beans = beanLocator.getAll(type);
            if (!beans.isEmpty()) {
                instance = beans.get(name);
                // If a bean with required name found, return it. Otherwise return first found.
                if (instance != null) {
                    return instance;
                } else {
                    return beans.values().iterator().next();
                }
            }
        }
        return null;
    }

    protected <T extends Screen> void assignValue(AnnotatedElement element, Object value, T screen) {
        // element is already marked as accessible in UiControllerReflectionInspector

        if (element instanceof Field) {
            Field field = (Field) element;

            try {
                field.set(screen, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("CDI - Unable to assign value to field " + field.getName(), e);
            }
        } else {
            Method method = (Method) element;

            Object[] params = new Object[1];
            params[0] = value;
            try {
                method.invoke(screen, params);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("CDI - Unable to assign value through setter "
                        + method.getName(), e);
            }
        }
    }
}
