package com.github.mihaildemidoff.itpoker.configuration;

import com.github.mihaildemidoff.itpoker.service.telegram.ITPokerBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import reactor.core.publisher.Sinks;

@Configuration
public class TelegramConfiguration {

    @Bean(destroyMethod = "stop")
    public BotSession itPokerBotSession(final ITPokerBot itPokerBot) throws Exception {
        final TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        return telegramBotsApi.registerBot(itPokerBot);
    }

    @Bean
    public Sinks.Many<Update> updatesSink() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }

}
