package com.github.mihaildemidoff.itpoker.service.telegram.handler;

import com.github.mihaildemidoff.itpoker.service.poll.PollService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChosenInlineQueryHandler implements UpdateHandler {

    private final PollService pollService;

    @Override
    @Transactional
    public Mono<Boolean> handle(final Update update, final AbsSender absSender) {
        return pollService
                .createPoll(Long.valueOf(update.getChosenInlineQuery().getResultId()), update.getChosenInlineQuery().getInlineMessageId(), update.getChosenInlineQuery().getFrom().getId(), update.getChosenInlineQuery().getQuery())
                .map(pollBO -> true);
    }

    @Override
    public boolean shouldHandle(final Update update) {
        return update.hasChosenInlineQuery();
    }
}
