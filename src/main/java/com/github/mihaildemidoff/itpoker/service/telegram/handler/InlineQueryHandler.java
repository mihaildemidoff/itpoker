package com.github.mihaildemidoff.itpoker.service.telegram.handler;

import com.github.mihaildemidoff.itpoker.model.bo.ButtonType;
import com.github.mihaildemidoff.itpoker.model.bo.DeckBO;
import com.github.mihaildemidoff.itpoker.model.bo.template.PollTemplateBO;
import com.github.mihaildemidoff.itpoker.service.deck.DeckService;
import com.github.mihaildemidoff.itpoker.service.telegram.KeyboardMarkupService;
import com.github.mihaildemidoff.itpoker.service.telegram.SenderHelper;
import com.github.mihaildemidoff.itpoker.service.telegram.TemplateService;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class InlineQueryHandler implements UpdateHandler {

    private final DeckService deckService;
    private final KeyboardMarkupService keyboardMarkupService;
    private final TemplateService templateService;
    private final SenderHelper senderHelper;

    @Override
    @Transactional
    public Mono<Boolean> handle(final Update update, final AbsSender absSender) {
        if (update.getInlineQuery().getQuery().isBlank()) {
            return Mono.empty();
        }
        return deckService.findAllDecks()
                .flatMap(deck -> buildQueryResult(update, deck))
                .collectList()
                .map(decks -> {
                    final AnswerInlineQuery answer = new AnswerInlineQuery(update.getInlineQuery().getId(), decks);
                    answer.setIsPersonal(false);
                    answer.setCacheTime(0);
                    return answer;
                })
                .flatMap(answer -> senderHelper.executeAsync(absSender, answer))
                .map(aBoolean -> aBoolean);
    }

    private Mono<InlineQueryResult> buildQueryResult(final Update update, final DeckBO deck) {
        return Mono.zip(keyboardMarkupService
                                .buildMarkup(deck.id(), List.of(ButtonType.VOTE, ButtonType.RESTART, ButtonType.FINISH)),
                        templateService.generateVoteTemplate(PollTemplateBO.builder()
                                .decision("")
                                .hasDecision(false)
                                .finished(false)
                                .taskName(update.getInlineQuery().getQuery())
                                .votes(List.of())
                                .build()))
                .map(t -> {
                    final InputTextMessageContent inputMessageContent = new InputTextMessageContent(t.getT2(), "HTML", true, List.of());
                    return new InlineQueryResultArticle(deck.id().toString(), deck.title(), inputMessageContent, t.getT1(), null, true, deck.description(), null, null, null);
                });
    }

    @Override
    public boolean shouldHandle(final Update update) {
        return update.hasInlineQuery();
    }
}
