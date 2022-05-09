package com.github.mihaildemidoff.itpoker.service.telegram.handler;

import com.github.mihaildemidoff.itpoker.service.deck.DeckService;
import com.github.mihaildemidoff.itpoker.service.telegram.KeyboardMarkupService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.bots.AbsSender;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class InlineQueryHandler implements UpdateHandler {

    private final DeckService deckService;
    private final KeyboardMarkupService keyboardMarkupService;

    @Override
    @Transactional
    public Mono<Boolean> handle(final Update update, final AbsSender absSender) {
        if (update.getInlineQuery().getQuery().isBlank()) {
            return Mono.empty();
        }
        return deckService.findAllDecks()
                .flatMap(deck -> keyboardMarkupService.buildMarkup(deck.id())
                        .map(buttons -> {
                            final InputTextMessageContent inputMessageContent = new InputTextMessageContent("Some message", "HTML", true, List.of());
                            return (InlineQueryResult) new InlineQueryResultArticle(deck.id().toString(), deck.title(), inputMessageContent, buttons, null, true, deck.description(), null, null, null);
                        }))
                .collectList()
                .map(decks -> {
                    final AnswerInlineQuery answer = new AnswerInlineQuery(update.getInlineQuery().getId(), decks);
                    answer.setIsPersonal(false);
                    answer.setCacheTime(0);
                    return answer;
                })
                .flatMap(new Function<AnswerInlineQuery, Mono<? extends Boolean>>() {
                    @Override
                    @SneakyThrows
                    public Mono<? extends Boolean> apply(final AnswerInlineQuery answer) {
                        return Mono.fromFuture(absSender.executeAsync(answer));
                    }
                })
                .map(aBoolean -> aBoolean);
    }

    @Override
    public boolean shouldHandle(final Update update) {
        return update.hasInlineQuery();
    }
}
