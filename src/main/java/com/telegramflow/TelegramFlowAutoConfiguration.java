package com.telegramflow;

import com.telegramflow.global.Messages;
import com.telegramflow.screens.DefaultScreenSelector;
import com.telegramflow.screens.ScreenSelector;
import com.telegramflow.screens.ScreenConfig;
import com.telegramflow.screens.ScreenScanner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.telegramflow")
public class TelegramFlowAutoConfiguration {

    @Bean(ScreenConfig.NAME)
    @ConditionalOnMissingBean
    public ScreenConfig screenConfig(ApplicationContext applicationContext) throws ClassNotFoundException {
        ScreenConfig screenConfig = new ScreenConfig();
        screenConfig.setScreenScanner(new ScreenScanner(applicationContext));
        screenConfig.initialize();
        return screenConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    public ScreenSelector screenSelector() {
        return new DefaultScreenSelector();
    }

    @Bean
    @ConditionalOnMissingBean
    public Messages messages(MessageSource messageSource) {
        return new Messages(messageSource);
    }

}
