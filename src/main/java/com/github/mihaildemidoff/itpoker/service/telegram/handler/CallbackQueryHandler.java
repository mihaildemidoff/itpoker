package com.github.mihaildemidoff.itpoker.service.telegram.handler;

import com.github.mihaildemidoff.itpoker.model.exception.PollAlreadyFinishedException;
import com.github.mihaildemidoff.itpoker.model.exception.PollNotFoundException;
import com.github.mihaildemidoff.itpoker.model.exception.UserNotAllowedException;
import com.github.mihaildemidoff.itpoker.service.deck.DeckOptionService;
import com.github.mihaildemidoff.itpoker.service.poll.PollLifecycleService;
import com.github.mihaildemidoff.itpoker.service.telegram.KeyboardMarkupService;
import com.github.mihaildemidoff.itpoker.service.vote.VoteService;
import io.github.mihaildemidoff.reactive.tg.bots.core.TelegramClient;
import io.github.mihaildemidoff.reactive.tg.bots.model.inline.CallbackQuery;
import io.github.mihaildemidoff.reactive.tg.bots.model.methods.AnswerCallbackQueryMethod;
import io.github.mihaildemidoff.reactive.tg.bots.model.update.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallbackQueryHandler implements UpdateHandler {
    private final VoteService voteService;
    private final DeckOptionService deckOptionService;
    private final KeyboardMarkupService keyboardMarkupService;
    private final PollLifecycleService pollLifecycleService;
    private final TelegramClient telegramClient;

    @Override
    @Transactional
    public Mono<Boolean> handle(final Update update) {
        return Mono.just(update.getCallbackQuery())
                .flatMap(callback -> keyboardMarkupService.getButtonType(callback.getData())
                        .flatMap(buttonType -> switch (buttonType) {
                            case VOTE -> vote(callback);
                            case FINISH -> finishPoll(callback);
                            case RESTART -> restartPoll(callback);
                        }))
                .onErrorResume(PollAlreadyFinishedException.class, e -> telegramClient.executeMethod(buildAnswer(update.getCallbackQuery(), "\uD83D\uDD34 Error: poll already finished")).thenReturn(false))
                .onErrorResume(PollNotFoundException.class, e -> telegramClient.executeMethod(buildAnswer(update.getCallbackQuery(), "\uD83D\uDD34 Error: poll not found")).thenReturn(false))
                .onErrorResume(UserNotAllowedException.class, e -> telegramClient.executeMethod(buildAnswer(update.getCallbackQuery(), "\uD83D\uDD34 Error: action not allowed for the user")).thenReturn(false))
                .onErrorResume(Exception.class, e -> telegramClient.executeMethod(buildAnswer(update.getCallbackQuery(), "\uD83D\uDD34 Error during processing request")).thenReturn(false));
    }

    private Mono<Boolean> restartPoll(final CallbackQuery callbackQuery) {
        return pollLifecycleService
                .restartPoll(callbackQuery.getInlineMessageId(), callbackQuery.getFrom().getId())
                .hasElement()
                .flatMap(unused -> telegramClient.executeMethod(buildAnswer(callbackQuery, "\uD83D\uDFE2 Poll restarted")))
                .map(unused -> true);
    }

    private Mono<Boolean> finishPoll(final CallbackQuery callbackQuery) {
        return pollLifecycleService
                .finishPoll(callbackQuery.getInlineMessageId(), callbackQuery.getFrom().getId())
                .hasElement()
                .flatMap(unused -> telegramClient.executeMethod(buildAnswer(callbackQuery, "\uD83D\uDFE2 Poll finished")))
                .map(unused -> true);
    }

    @Override
    public boolean shouldHandle(final Update update) {
        return update.getCallbackQuery() != null;
    }

    private Mono<Boolean> vote(final CallbackQuery callbackQuery) {
        return voteService
                .createOrUpdateVote(callbackQuery.getInlineMessageId(), Long.valueOf(callbackQuery.getData()), callbackQuery.getFrom().getId(), callbackQuery.getFrom().getUsername(), callbackQuery.getFrom().getFirstName(), callbackQuery.getFrom().getLastName())
                .flatMap(vote -> deckOptionService.findById(vote.deckOptionId())
                        .map(deckOption -> buildAnswer(callbackQuery, "\uD83D\uDFE2 Your vote: " + deckOption.text())))
                .flatMap(telegramClient::executeMethod)
                .thenReturn(true);
    }

    private AnswerCallbackQueryMethod buildAnswer(final CallbackQuery callbackQuery,
                                                  final String text) {
        return AnswerCallbackQueryMethod.builder()
                .callbackQueryId(callbackQuery.getId())
                .text(text)
                .showAlert(false)
                .build();
    }

}
