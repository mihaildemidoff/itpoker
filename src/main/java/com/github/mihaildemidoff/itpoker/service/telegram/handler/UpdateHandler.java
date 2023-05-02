package com.github.mihaildemidoff.itpoker.service.telegram.handler;

import io.github.mihaildemidoff.reactive.tg.bots.model.update.Update;
import reactor.core.publisher.Mono;

public interface UpdateHandler {

    Mono<Boolean> handle(Update update);

    boolean shouldHandle(Update update);

}
