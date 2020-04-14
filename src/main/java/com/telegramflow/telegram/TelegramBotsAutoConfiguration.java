package com.telegramflow.telegram;

import com.telegramflow.global.Messages;
import com.telegramflow.security.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Objects;
import java.util.Set;

@Configuration
@ConditionalOnProperty(prefix = "bot", name = "token")
@EnableConfigurationProperties({BotProperties.class, ProxyProperties.class})
public class TelegramBotsAutoConfiguration {

    public TelegramBotsAutoConfiguration() {
        ApiContextInitializer.init();
    }

    @Bean
    @ConditionalOnMissingBean
    public TelegramBotsApi telegramBotsApi(BotProperties botProperties) throws TelegramApiRequestException {
        if (botProperties.getType() == BotType.WEBHOOK) {
            Objects.requireNonNull(botProperties.getExternalUrl(),
                    "Property 'bot.externalUrl' required for webhook bot");
            Objects.requireNonNull(botProperties.getInternalUrl(),
                    "Property 'bot.internalUrl' required for webhook bot");
            if (botProperties.getKeyStore() != null && botProperties.getPathToCertificate() != null) {
                return new TelegramBotsApi(botProperties.getKeyStore(),
                        botProperties.getKeyStorePassword(),
                        botProperties.getExternalUrl(),
                        botProperties.getInternalUrl(),
                        botProperties.getPathToCertificate());
            } else if (botProperties.getPathToCertificate() == null) {
                return new TelegramBotsApi(botProperties.getKeyStore(),
                        botProperties.getKeyStorePassword(),
                        botProperties.getExternalUrl(),
                        botProperties.getInternalUrl());
            } else {
                return new TelegramBotsApi(botProperties.getExternalUrl(),
                        botProperties.getInternalUrl());
            }
        } else {
            return new TelegramBotsApi();
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultBotOptions defaultBotOptions(ProxyProperties proxyProperties) {
        DefaultBotOptions options = ApiContext.getInstance(DefaultBotOptions.class);
        if (proxyProperties.getType() != null) {
            options.setProxyType(proxyProperties.getType());
            options.setProxyHost(proxyProperties.getHost());
            options.setProxyPort(proxyProperties.getPort());

            if (!StringUtils.isEmpty(proxyProperties.getUsername())) {
                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(proxyProperties.getUsername(),
                                proxyProperties.getPassword().toCharArray());
                    }
                });
            }
        }
        return options;
    }

    @Bean
    @ConditionalOnMissingBean
    public UpdateReceiver updateReceiver(DefaultBotOptions defaultBotOptions,
                                         BotProperties botProperties) {
        if (botProperties.getType() == BotType.WEBHOOK) {
            return new DefaultWebhookBot(defaultBotOptions,
                    botProperties.getToken(),
                    botProperties.getUsername());
        } else {
            return new DefaultLongPollingBot(defaultBotOptions,
                    botProperties.getToken(),
                    botProperties.getUsername());
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public TelegramBotsManager telegramBotsManager(TelegramBotsApi telegramBotsApi, Set<UpdateReceiver> updateReceivers)
            throws TelegramApiRequestException {
        TelegramBotsManager telegramBotManager = new TelegramBotsManager(telegramBotsApi);
        for(UpdateReceiver updateReceiver : updateReceivers) {
            telegramBotManager.register(updateReceiver);
        }
        return telegramBotManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public Authentication authentication(UserProvider userProvider, RoleProvider roleProvider,
                                         SessionProvider sessionProvider, TelegramBot telegramBot, Messages messages) {
        return new SharePhoneAuthentication(userProvider, roleProvider, sessionProvider, telegramBot, messages);
    }

    @Bean
    @ConditionalOnMissingBean
    public TelegramBot telegramBot(DefaultBotOptions defaultBotOptions, BotProperties botProperties) {
        return new TelegramBot(defaultBotOptions, botProperties.getToken());
    }

}
