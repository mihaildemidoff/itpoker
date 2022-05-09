package com.github.mihaildemidoff.itpoker.service.telegram;

import com.github.mihaildemidoff.itpoker.model.exception.HandlerNotFoundException;
import com.github.mihaildemidoff.itpoker.properties.TelegramProperties;
import com.github.mihaildemidoff.itpoker.service.telegram.handler.UpdateHandler;
import com.google.common.collect.MoreCollectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ITPokerBot extends TelegramLongPollingBot {

    private final Sinks.Many<Update> updatesSink;
    private final TelegramProperties telegramProperties;
    private final List<UpdateHandler> updateHandlers;

    @PostConstruct
    public void start() {
        updatesSink
                .asFlux()
                .doOnNext(update -> {
                    log.error("Update {}", update);
                })
                .flatMap(update -> updateHandlers.stream()
                        .filter(handler -> handler.shouldHandle(update))
                        .collect(MoreCollectors.toOptional())
                        .map(handler -> handler.handle(update, ITPokerBot.this))
                        .orElse(Mono.error(new HandlerNotFoundException())))
                .onErrorContinue((throwable, o) -> {
                    log.error("Error during processing update", throwable);
                })
                .subscribe();
    }

    @Override
    public void onUpdateReceived(final Update update) {
        updatesSink.emitNext(update, (signalType, emitResult) -> true);
    }

    @Override
    public void clearWebhook() {

    }

    @Override
    public String getBotUsername() {
        return telegramProperties.getName();
    }

    @Override
    public String getBotToken() {
        return telegramProperties.getToken();
    }

}
