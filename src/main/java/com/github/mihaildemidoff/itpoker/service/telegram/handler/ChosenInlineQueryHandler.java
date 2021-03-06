package com.github.mihaildemidoff.itpoker.service.telegram.handler;

import com.github.mihaildemidoff.itpoker.service.poll.PollService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChosenInlineQueryHandler implements UpdateHandler {

    private final PollService pollService;

    @Override
    @Transactional
    public Mono<Boolean> handle(final Update update, final AbsSender absSender) {
        return Mono.just(update.getChosenInlineQuery())
                .flatMap(query -> pollService.createPoll(Long.valueOf(query.getResultId()),
                        query.getInlineMessageId(),
                        query.getFrom().getId(),
                        query.getQuery()))
                .map(poll -> true)
                .doOnError(error -> log.error("Error occurred during processing chosen inline query with id: " + update.getUpdateId(), error))
                .onErrorReturn(false);
    }

    @Override
    public boolean shouldHandle(final Update update) {
        return update.hasChosenInlineQuery();
    }
}
