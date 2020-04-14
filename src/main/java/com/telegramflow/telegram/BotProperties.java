package com.telegramflow.telegram;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bot")
public class BotProperties {

    private String token;

    private String username;

    private BotType type;

    private String internalUrl;

    private String externalUrl;

    private String keyStore;

    private String keyStorePassword;

    private String pathToCertificate;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BotType getType() {
        return type;
    }

    public void setType(BotType type) {
        this.type = type;
    }

    public String getInternalUrl() {
        return internalUrl;
    }

    public void setInternalUrl(String internalUrl) {
        this.internalUrl = internalUrl;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getPathToCertificate() {
        return pathToCertificate;
    }

    public void setPathToCertificate(String pathToCertificate) {
        this.pathToCertificate = pathToCertificate;
    }
}
