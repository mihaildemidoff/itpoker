package com.github.mihaildemidoff.itpoker.service.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Mono;

import java.io.Serializable;

@Service
@Slf4j
public class SenderHelper {

    public <T extends Serializable, Method extends BotApiMethod<T>> Mono<T> executeAsync(final AbsSender absSender,
                                                                                         final Method method) {
        try {
            return Mono.fromFuture(absSender.executeAsync(method));
        } catch (final TelegramApiException e) {
            return Mono.error(e);
        }
    }

}
