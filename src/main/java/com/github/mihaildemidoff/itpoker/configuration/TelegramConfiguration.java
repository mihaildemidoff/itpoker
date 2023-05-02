package com.github.mihaildemidoff.itpoker.configuration;

import com.github.mihaildemidoff.itpoker.properties.TelegramProperties;
import io.github.mihaildemidoff.reactive.tg.bots.core.TelegramClient;
import io.github.mihaildemidoff.reactive.tg.bots.core.properties.TelegramBotProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class TelegramConfiguration {

    @Bean
    public TelegramClient telegramClient(final TelegramProperties appProperties) {
        final TelegramBotProperties properties = new TelegramBotProperties(appProperties.getToken(), Duration.ofSeconds(10), Duration.ofSeconds(50), "https://api.telegram.org/");
        return new TelegramClient(properties);
    }

}
