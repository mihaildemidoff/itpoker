package com.github.mihaildemidoff.itpoker.service.telegram.handler;

import com.github.mihaildemidoff.itpoker.model.bo.ButtonType;
import com.github.mihaildemidoff.itpoker.model.bo.DeckBO;
import com.github.mihaildemidoff.itpoker.model.bo.template.PollTemplateBO;
import com.github.mihaildemidoff.itpoker.service.deck.DeckService;
import com.github.mihaildemidoff.itpoker.service.telegram.KeyboardMarkupService;
import com.github.mihaildemidoff.itpoker.service.telegram.TemplateService;
import io.github.mihaildemidoff.reactive.tg.bots.core.client.api.TelegramClient;
import io.github.mihaildemidoff.reactive.tg.bots.model.enums.ParseMode;
import io.github.mihaildemidoff.reactive.tg.bots.model.inline.InlineQuery;
import io.github.mihaildemidoff.reactive.tg.bots.model.inline.content.InputTextMessageContent;
import io.github.mihaildemidoff.reactive.tg.bots.model.inline.result.InlineQueryResultArticle;
import io.github.mihaildemidoff.reactive.tg.bots.model.methods.inline.AnswerInlineQueryMethod;
import io.github.mihaildemidoff.reactive.tg.bots.model.reply.InlineKeyboardMarkup;
import io.github.mihaildemidoff.reactive.tg.bots.model.update.Update;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class InlineQueryHandlerTest {

    @InjectMocks
    private InlineQueryHandler handler;
    @Mock
    private Update update;
    @Mock
    private InlineQuery inlineQuery;
    @Mock
    private DeckService deckService;
    @Mock
    private KeyboardMarkupService keyboardMarkupService;
    @Mock
    private TemplateService templateService;
    @Mock
    private TelegramClient telegramClient;

    @Test
    void testSuccessHandle() {
        Mockito.when(update.getInlineQuery())
                .thenReturn(inlineQuery);
        final String taskName = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(inlineQuery.getQuery())
                .thenReturn(taskName);
        final String queryId = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(inlineQuery.getId())
                .thenReturn(queryId);
        final DeckBO deck = DeckBO.builder()
                .id(RandomUtils.nextLong())
                .title(RandomStringUtils.randomAlphabetic(10))
                .description(RandomStringUtils.randomAlphabetic(10))
                .build();
        final List<DeckBO> decks = List.of(deck);
        Mockito.when(deckService.findAllDecks())
                .thenReturn(Flux.fromIterable(decks));
        final InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder().build();
        Mockito.when(keyboardMarkupService.buildMarkup(ArgumentMatchers.eq(deck.id()), ArgumentMatchers.eq(List.of(ButtonType.VOTE, ButtonType.RESTART, ButtonType.FINISH))))
                .thenReturn(Mono.just(inlineKeyboardMarkup));
        final String template = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(templateService.generateVoteTemplate(ArgumentMatchers.eq(PollTemplateBO.builder()
                        .decision("")
                        .hasDecision(false)
                        .finished(false)
                        .taskName(taskName)
                        .votes(List.of())
                        .build())))
                .thenReturn(Mono.just(template));
        final InputTextMessageContent inputMessageContent = InputTextMessageContent.builder()
                .messageText(template)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .build();
        final InlineQueryResultArticle article = InlineQueryResultArticle.builder()
                .id(deck.id().toString())
                .title(deck.title())
                .inputMessageContent(inputMessageContent)
                .replyMarkup(inlineKeyboardMarkup)
                .hideUrl(true)
                .description(deck.description())
                .build();
        final AnswerInlineQueryMethod expectedAnswer = AnswerInlineQueryMethod.builder()
                .inlineQueryId(queryId)
                .results(List.of(article))
                .isPersonal(false)
                .cacheTime(0L)
                .build();
        final boolean expectedResult = RandomUtils.nextBoolean();
        Mockito.when(telegramClient.executeMethod(ArgumentMatchers.eq(expectedAnswer)))
                .thenReturn(Mono.just(expectedResult));
        StepVerifier.create(handler.handle(update))
                .expectSubscription()
                .expectNext(expectedResult)
                .verifyComplete();
    }

    @Test
    void testBlankQuery() {
        Mockito.when(update.getInlineQuery())
                .thenReturn(inlineQuery);
        Mockito.when(inlineQuery.getQuery())
                .thenReturn("       ");
        StepVerifier.create(handler.handle(update))
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void testShouldHandle() {
        Mockito.when(update.getInlineQuery())
                .thenReturn(InlineQuery.builder()
                        .build());
        assertThat(handler.shouldHandle(update), CoreMatchers.is(true));
    }
}
