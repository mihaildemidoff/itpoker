package com.github.mihaildemidoff.itpoker.service.telegram.handler;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import reactor.core.publisher.Mono;

public interface UpdateHandler {

    Mono<Boolean> handle(Update update, AbsSender absSender);

    boolean shouldHandle(Update update);

}
