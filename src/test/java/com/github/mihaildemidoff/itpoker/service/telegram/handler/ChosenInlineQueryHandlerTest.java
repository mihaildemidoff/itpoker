package com.github.mihaildemidoff.itpoker.service.telegram.handler;

import com.github.mihaildemidoff.itpoker.model.bo.PollBO;
import com.github.mihaildemidoff.itpoker.service.poll.PollService;
import io.github.mihaildemidoff.reactive.tg.bots.model.inline.ChosenInlineResult;
import io.github.mihaildemidoff.reactive.tg.bots.model.update.Update;
import io.github.mihaildemidoff.reactive.tg.bots.model.user.User;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class ChosenInlineQueryHandlerTest {

    @InjectMocks
    private ChosenInlineQueryHandler handler;
    @Mock
    private PollService pollService;
    @Mock
    private Update update;


    @Test
    void testHandleError() {
        final ChosenInlineResult chosenInlineQuery = Mockito.mock(ChosenInlineResult.class);
        Mockito.when(update.getChosenInlineResult())
                .thenReturn(chosenInlineQuery);
        final Long deckId = RandomUtils.nextLong();
        final Long userId = RandomUtils.nextLong();
        final String messageId = RandomStringUtils.randomAlphabetic(10);
        final String query = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(chosenInlineQuery.getResultId())
                .thenReturn(deckId.toString());
        Mockito.when(chosenInlineQuery.getInlineMessageId())
                .thenReturn(messageId);
        final User user = Mockito.mock(User.class);
        Mockito.when(chosenInlineQuery.getFrom())
                .thenReturn(user);
        Mockito.when(user.getId())
                .thenReturn(userId);
        Mockito.when(chosenInlineQuery.getQuery())
                .thenReturn(query);
        Mockito.when(pollService.createPoll(ArgumentMatchers.eq(deckId),
                        ArgumentMatchers.eq(messageId), ArgumentMatchers.eq(userId), ArgumentMatchers.eq(query)))
                .thenReturn(Mono.error(IllegalArgumentException::new));
        StepVerifier.create(handler.handle(update))
                .expectSubscription()
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void testHandleSuccess() {
        final ChosenInlineResult chosenInlineQuery = Mockito.mock(ChosenInlineResult.class);
        Mockito.when(update.getChosenInlineResult())
                .thenReturn(chosenInlineQuery);
        final Long deckId = RandomUtils.nextLong();
        final Long userId = RandomUtils.nextLong();
        final String messageId = RandomStringUtils.randomAlphabetic(10);
        final String query = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(chosenInlineQuery.getResultId())
                .thenReturn(deckId.toString());
        Mockito.when(chosenInlineQuery.getInlineMessageId())
                .thenReturn(messageId);
        final User user = Mockito.mock(User.class);
        Mockito.when(chosenInlineQuery.getFrom())
                .thenReturn(user);
        Mockito.when(user.getId())
                .thenReturn(userId);
        Mockito.when(chosenInlineQuery.getQuery())
                .thenReturn(query);
        Mockito.when(pollService.createPoll(ArgumentMatchers.eq(deckId),
                        ArgumentMatchers.eq(messageId), ArgumentMatchers.eq(userId), ArgumentMatchers.eq(query)))
                .thenReturn(Mono.just(PollBO.builder().build()));
        StepVerifier.create(handler.handle(update))
                .expectSubscription()
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void testShouldHandle() {
        Mockito.when(update.getChosenInlineResult())
                .thenReturn(ChosenInlineResult.builder()
                        .build());
        assertThat(handler.shouldHandle(update), CoreMatchers.is(true));
    }

}
