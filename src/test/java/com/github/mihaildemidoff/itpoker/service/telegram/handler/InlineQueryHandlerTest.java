package com.github.mihaildemidoff.itpoker.service.telegram.handler;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import reactor.test.StepVerifier;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class InlineQueryHandlerTest {

    @InjectMocks
    private InlineQueryHandler handler;
    @Mock
    private AbsSender absSender;
    @Mock
    private Update update;
    @Mock
    private InlineQuery inlineQuery;

    @Test
    void testBlankQuery() {
        Mockito.when(update.getInlineQuery())
                .thenReturn(inlineQuery);
        Mockito.when(inlineQuery.getQuery())
                .thenReturn("       ");
        StepVerifier.create(handler.handle(update, absSender))
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void testShouldHandle() {
        Mockito.when(update.hasInlineQuery())
                .thenReturn(true);
        assertThat(handler.shouldHandle(update), CoreMatchers.is(true));
    }
}
