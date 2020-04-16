package com.telegramflow.screens.xml.layout;

import com.telegramflow.components.*;
import com.telegramflow.global.BeanLocator;
import com.telegramflow.screens.ScreenInfo;
import org.dom4j.Element;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component(LayoutLoader.NAME)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class LayoutLoader {

    public static final String NAME = "tf$LayoutLoader";

    // TODO inject messages resources

    protected BeanLocator beanLocator;

    protected Layout layout;

    protected ScreenInfo screenInfo;

    public LayoutLoader() {
        this.layout = new Layout();
    }

    public void setBeanLocator(BeanLocator beanLocator) {
        this.beanLocator = beanLocator;
    }

    public Layout load(Element element, ScreenInfo screenInfo) {
        loadMessage(element);
        loadKeyboard(element);

        return layout;
    }

    protected void loadMessage(Element element) {
        if (element == null) {
            return;
        }
        Element messageElement = element.element("message");
        if (messageElement == null) {
            throw new RuntimeException("Screen must contain Message element: " + screenInfo.getId());
        }

        String formatValue = messageElement.attributeValue("format");
        if (formatValue != null) {
            layout.getMessage().setFormat(Message.Format.valueOf(formatValue));
        }
        layout.getMessage().setText(messageElement.getText());

        // TODO set text format MARKDOWN and so on
    }

    protected void loadKeyboard(Element element) {
        if (element == null) {
            return;
        }

        Element keyboardElement = element.element("keyboard");
        if (keyboardElement == null) {
            return;
        }

        String removeKeyboardValue = keyboardElement.attributeValue("removeKeyboard");
        if (removeKeyboardValue != null) {
            layout.getKeyboard().setRemoveKeyboard(Boolean.valueOf(removeKeyboardValue));
        }

        String oneTimeKeyboardValue = keyboardElement.attributeValue("oneTimeKeyboard");
        if (oneTimeKeyboardValue != null) {
            layout.getKeyboard().setOneTimeKeyboard(Boolean.valueOf(oneTimeKeyboardValue));
        }

        String resizeKeyboardValue = keyboardElement.attributeValue("resizeKeyboard");
        if (resizeKeyboardValue != null) {
            layout.getKeyboard().setResizeKeyboard(Boolean.valueOf(resizeKeyboardValue));
        }

        String columnsValue = keyboardElement.attributeValue("columns");
        Integer columns = columnsValue != null ? Integer.valueOf(columnsValue) : null;
        layout.getKeyboard().setColumns(columns);

        for(Element buttonElement : keyboardElement.elements("button")) {
            Button button = loadButton(buttonElement);
            layout.getKeyboard().addButton(button);
        }

        for(Element rowElement : keyboardElement.elements("row")) {
            ButtonRow buttonRow = new ButtonRow();
            for(Element buttonElement : rowElement.elements("button")) {
                buttonRow.add(loadButton(buttonElement));
            }
            layout.getKeyboard().addButtonRow(buttonRow);
        }

    }

    protected Button loadButton(Element element) {
        String id = element.attributeValue("id");
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        // TODO resource message
        String caption = element.attributeValue("caption");

        String visibleValue = element.attributeValue("visible");
        boolean visible = visibleValue == null || Boolean.parseBoolean(visibleValue);

        String invokeMethod = element.attributeValue("invoke");
        String transitTo = element.attributeValue("transitTo");
        AbstractButton button = null;
        if (invokeMethod != null) {
            button = new InvokeButton(layout, id, caption, invokeMethod);
            button.setVisible(visible);
        } else if (transitTo != null) {
            button = new TransitButton(layout, id, caption, transitTo);
            button.setVisible(visible);
        }
        return button;
    }
}
