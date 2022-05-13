package com.github.mihaildemidoff.itpoker.service.telegram.handler;

import com.github.mihaildemidoff.itpoker.model.exception.PollAlreadyFinishedException;
import com.github.mihaildemidoff.itpoker.model.exception.PollNotFoundException;
import com.github.mihaildemidoff.itpoker.service.deck.DeckOptionService;
import com.github.mihaildemidoff.itpoker.service.poll.PollLifecycleService;
import com.github.mihaildemidoff.itpoker.service.telegram.KeyboardMarkupService;
import com.github.mihaildemidoff.itpoker.service.telegram.SenderHelper;
import com.github.mihaildemidoff.itpoker.service.vote.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallbackQueryHandler implements UpdateHandler {
    private final VoteService voteService;
    private final DeckOptionService deckOptionService;
    private final KeyboardMarkupService keyboardMarkupService;
    private final PollLifecycleService pollLifecycleService;
    private final SenderHelper senderHelper;

    @Override
    @Transactional
    public Mono<Boolean> handle(final Update update, final AbsSender absSender) {
        return Mono.just(update.getCallbackQuery())
                .flatMap(callback -> keyboardMarkupService.getButtonType(callback.getData())
                        .flatMap(buttonType -> switch (buttonType) {
                            case VOTE -> vote(callback, absSender);
                            case FINISH -> finishPoll(callback, absSender);
                            case RESTART -> restartPoll(callback, absSender);
                        }))
                .onErrorResume(PollAlreadyFinishedException.class, e -> senderHelper.executeAsync(absSender, buildAnswer(update.getCallbackQuery(), "Error: poll already finished")).thenReturn(false))
                .onErrorResume(PollNotFoundException.class, e -> senderHelper.executeAsync(absSender, buildAnswer(update.getCallbackQuery(), "Error: poll not found")).thenReturn(false))
                .onErrorResume(Exception.class, e -> senderHelper.executeAsync(absSender, buildAnswer(update.getCallbackQuery(), "Error during processing request")).thenReturn(false));
    }

    private Mono<Boolean> restartPoll(final CallbackQuery callbackQuery,
                                      final AbsSender absSender) {
        return pollLifecycleService
                .restartPoll(callbackQuery.getInlineMessageId(), callbackQuery.getFrom().getId())
                .then(senderHelper.executeAsync(absSender, buildAnswer(callbackQuery, "Poll restarted")))
                .thenReturn(true);
    }

    private Mono<Boolean> finishPoll(final CallbackQuery callbackQuery,
                                     final AbsSender absSender) {
        return pollLifecycleService.finishPoll(callbackQuery.getInlineMessageId(), callbackQuery.getFrom().getId())
                .then(senderHelper.executeAsync(absSender, buildAnswer(callbackQuery, "Poll finished")))
                .thenReturn(true);
    }

    @Override
    public boolean shouldHandle(final Update update) {
        return update.hasCallbackQuery();
    }

    private Mono<Boolean> vote(final CallbackQuery callbackQuery,
                               final AbsSender absSender) {
        return voteService
                .createOrUpdateVote(callbackQuery.getInlineMessageId(), Long.valueOf(callbackQuery.getData()), callbackQuery.getFrom().getId(), callbackQuery.getFrom().getUserName(), callbackQuery.getFrom().getFirstName(), callbackQuery.getFrom().getLastName())
                .flatMap(vote -> deckOptionService.findById(vote.deckOptionId())
                        .map(deckOption -> buildAnswer(callbackQuery, "Your vote: " + deckOption.text())))
                .flatMap(answerCallbackQuery -> senderHelper.executeAsync(absSender, answerCallbackQuery))
                .thenReturn(true);
    }

    private AnswerCallbackQuery buildAnswer(final CallbackQuery callbackQuery,
                                            final String text) {
        return AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId())
                .text(text)
                .showAlert(false)
                .build();
    }

}
