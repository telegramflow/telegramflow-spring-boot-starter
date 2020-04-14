package com.telegramflow.components;

import java.util.Objects;

public abstract class AbstractButton implements Button {

    protected Layout layout;

    protected String id;

    protected String caption;

    protected boolean visible = true;

    protected boolean requestContact;

    protected boolean requestLocation;

    public AbstractButton() {
    }

    public AbstractButton(Layout layout, String id) {
        this.layout = layout;
        this.id = id;
    }

    public AbstractButton(Layout layout, String id, String caption) {
        this.layout = layout;
        this.id = id;
        this.caption = caption;
    }

    public Layout getLayout() {
        return layout;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isRequestContact() {
        return requestContact;
    }

    public void setRequestContact(boolean requestContact) {
        this.requestContact = requestContact;
    }

    @Override
    public boolean isRequestLocation() {
        return requestLocation;
    }

    public void setRequestLocation(boolean requestLocation) {
        this.requestLocation = requestLocation;
    }


}
