package com.telegramflow.screens;

import com.telegramflow.components.Layout;
import com.telegramflow.global.BeanLocator;
import com.telegramflow.screens.xml.ScreenXmlLoader;
import com.telegramflow.screens.xml.layout.LayoutLoader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;

@Component(ScreenManager.NAME)
public class ScreenManager {

    public static final String NAME = "tf$ScreenManager";

    @Inject
    protected UiControllerDependencyInjector dependencyInjector;

    @Inject
    protected ScreenXmlLoader screenXmlLoader;

    @Inject
    protected BeanLocator beanLocator;

    public <T extends Screen> T create(ScreenInfo screenInfo) {

        Element element = loadScreenXml(screenInfo);

        //noinspection unchecked
        Class<T> screenClass = (Class<T>) screenInfo.getScreenClass();

        T controller = createController(screenClass);
        controller.setId(screenInfo.getId());

        loadLayout(element, screenInfo, controller);

        dependencyInjector.inject(controller);

        controller.init();

        return controller;
    }

    protected <T extends Screen> T createController(Class<T> screenClass) {
        T controller;
        try {
            controller = ConstructorUtils.invokeConstructor(screenClass);
        } catch (NoSuchMethodException | InstantiationException
                | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Unable to create instance of screen class " + screenClass);
        }

        return controller;
    }

    @Nullable
    protected Element loadScreenXml(ScreenInfo screenInfo) {
        String templatePath = screenInfo.getTemplatePath();

        if (StringUtils.isNotEmpty(templatePath)) {
            return screenXmlLoader.load(templatePath);
        }

        return null;
    }

    protected <T extends Screen> void loadLayout(Element element, ScreenInfo screenInfo, T controller) {
//        if (screenInfo.getTemplate() != null) {
//            String messagesPack = element.attributeValue("messagesPack");
//            if (messagesPack != null) {
//                componentLoaderContext.setMessagesPack(messagesPack);
//            } else {
//                componentLoaderContext.setMessagesPack(getMessagePack(windowInfo.getTemplate()));
//            }
//        }

        LayoutLoader layoutLoader = beanLocator.getPrototype(LayoutLoader.NAME);
        layoutLoader.setBeanLocator(beanLocator);

        Layout layout = layoutLoader.load(element, screenInfo);
        controller.setLayout(layout);
    }

}
