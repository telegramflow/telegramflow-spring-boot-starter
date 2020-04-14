package com.telegramflow.telegram;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@ConfigurationProperties(prefix = "proxy")
public class ProxyProperties {

    private DefaultBotOptions.ProxyType type;

    private String host;

    private Integer port;

    private String username;

    private String password;

    public DefaultBotOptions.ProxyType getType() {
        return type;
    }

    public void setType(DefaultBotOptions.ProxyType type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
