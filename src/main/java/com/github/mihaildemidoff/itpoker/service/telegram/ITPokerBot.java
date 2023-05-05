package com.github.mihaildemidoff.itpoker.service.telegram;

import com.github.mihaildemidoff.itpoker.service.telegram.handler.UpdateHandler;
import com.google.common.collect.MoreCollectors;
import io.github.mihaildemidoff.reactive.tg.bots.core.client.api.TelegramPoller;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ITPokerBot {

    private final TelegramPoller telegramPoller;
    private final List<UpdateHandler> updateHandlers;
    private Disposable updatesDisposable;

    @EventListener
    public void onApplicationEvent(final ApplicationReadyEvent applicationReadyEvent) {
        updatesDisposable = telegramPoller.getUpdatesPublisher(List.of())
                .doOnNext(update -> log.info("Received update from telegram {}", update))
                .flatMap(update -> updateHandlers.stream()
                        .filter(handler -> handler.shouldHandle(update))
                        .collect(MoreCollectors.toOptional())
                        .map(handler -> handler.handle(update))
                        .orElse(Mono.empty())
                        .doOnNext(result -> log.info("Processed update: " + update.getUpdateId() + " with result: " + result)), 5)
                .onErrorContinue((throwable, o) -> log.error("Error during processing update", throwable))
                .subscribe();
    }

    @PreDestroy
    public void onDestroy() {
        if (updatesDisposable != null) {
            updatesDisposable.dispose();
        }
    }

}
