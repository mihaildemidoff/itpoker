package com.github.mihaildemidoff.itpoker.service.telegram.handler;

import com.github.mihaildemidoff.itpoker.model.bo.ButtonType;
import com.github.mihaildemidoff.itpoker.model.bo.DeckBO;
import com.github.mihaildemidoff.itpoker.model.bo.template.PollTemplateBO;
import com.github.mihaildemidoff.itpoker.service.deck.DeckService;
import com.github.mihaildemidoff.itpoker.service.telegram.KeyboardMarkupService;
import com.github.mihaildemidoff.itpoker.service.telegram.TemplateService;
import io.github.mihaildemidoff.reactive.tg.bots.core.TelegramClient;
import io.github.mihaildemidoff.reactive.tg.bots.model.enums.ParseMode;
import io.github.mihaildemidoff.reactive.tg.bots.model.inline.content.InputTextMessageContent;
import io.github.mihaildemidoff.reactive.tg.bots.model.inline.result.InlineQueryResult;
import io.github.mihaildemidoff.reactive.tg.bots.model.inline.result.InlineQueryResultArticle;
import io.github.mihaildemidoff.reactive.tg.bots.model.methods.inline.AnswerInlineQueryMethod;
import io.github.mihaildemidoff.reactive.tg.bots.model.update.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InlineQueryHandler implements UpdateHandler {

    private final DeckService deckService;
    private final KeyboardMarkupService keyboardMarkupService;
    private final TemplateService templateService;
    private final TelegramClient client;

    @Override
    @Transactional
    public Mono<Boolean> handle(final Update update) {
        if (update.getInlineQuery().getQuery().isBlank()) {
            return Mono.empty();
        }
        return deckService.findAllDecks()
                .flatMap(deck -> buildQueryResult(update, deck))
                .collectList()
                .map(decks -> AnswerInlineQueryMethod.builder()
                        .inlineQueryId(update.getInlineQuery().getId())
                        .results(decks)
                        .isPersonal(false)
                        .cacheTime(0L)
                        .build())
                .flatMap(client::executeMethod)
                .map(aBoolean -> aBoolean);
    }

    private Mono<InlineQueryResult> buildQueryResult(final Update update,
                                                     final DeckBO deck) {
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
                    final InputTextMessageContent inputMessageContent = InputTextMessageContent.builder()
                            .messageText(t.getT2())
                            .parseMode(ParseMode.HTML)
                            .disableWebPagePreview(true)
                            .build();
                    return InlineQueryResultArticle.builder()
                            .id(deck.id().toString())
                            .title(deck.title())
                            .inputMessageContent(inputMessageContent)
                            .replyMarkup(t.getT1())
                            .hideUrl(true)
                            .description(deck.description())
                            .build();

                });
    }

    @Override
    public boolean shouldHandle(final Update update) {
        return update.getInlineQuery() != null;
    }
}
