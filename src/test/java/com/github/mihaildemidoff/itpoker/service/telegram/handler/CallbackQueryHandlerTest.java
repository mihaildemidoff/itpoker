package com.github.mihaildemidoff.itpoker.service.telegram.handler;

import com.github.mihaildemidoff.itpoker.model.bo.ButtonType;
import com.github.mihaildemidoff.itpoker.model.bo.DeckOptionBO;
import com.github.mihaildemidoff.itpoker.model.bo.VoteBO;
import com.github.mihaildemidoff.itpoker.service.deck.DeckOptionService;
import com.github.mihaildemidoff.itpoker.service.poll.PollLifecycleService;
import com.github.mihaildemidoff.itpoker.service.telegram.KeyboardMarkupService;
import com.github.mihaildemidoff.itpoker.service.vote.VoteService;
import io.github.mihaildemidoff.reactive.tg.bots.core.client.api.TelegramClient;
import io.github.mihaildemidoff.reactive.tg.bots.model.inline.CallbackQuery;
import io.github.mihaildemidoff.reactive.tg.bots.model.methods.AnswerCallbackQueryMethod;
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
class CallbackQueryHandlerTest {

    @InjectMocks
    private CallbackQueryHandler handler;
    @Mock
    private VoteService voteService;
    @Mock
    private DeckOptionService deckOptionService;
    @Mock
    private KeyboardMarkupService keyboardMarkupService;
    @Mock
    private PollLifecycleService pollLifecycleService;
    @Mock
    private Update update;
    @Mock
    private CallbackQuery callbackQuery;
    @Mock
    private TelegramClient telegramClient;

    @Test
    void testVoteSuccess() {
        final long deckOptionId = RandomUtils.nextLong();
        final String callbackData = String.valueOf(deckOptionId);
        final String inlineMessageId = RandomStringUtils.randomAlphabetic(10);
        final long userId = RandomUtils.nextLong();
        final String callbackQueryId = RandomStringUtils.randomAlphabetic(10);
        final String username = RandomStringUtils.randomAlphabetic(10);
        final String firstName = RandomStringUtils.randomAlphabetic(10);
        final String lastName = RandomStringUtils.randomAlphabetic(10);
        setupUpdate(callbackData, inlineMessageId, userId, callbackQueryId, username, firstName, lastName);
        Mockito.when(keyboardMarkupService.getButtonType(ArgumentMatchers.eq(callbackData)))
                .thenReturn(Mono.just(ButtonType.VOTE));
        Mockito.when(voteService.createOrUpdateVote(ArgumentMatchers.eq(inlineMessageId), ArgumentMatchers.eq(deckOptionId), ArgumentMatchers.eq(userId), ArgumentMatchers.eq(username), ArgumentMatchers.eq(firstName), ArgumentMatchers.eq(lastName)))
                .thenReturn(Mono.just(VoteBO.builder()
                        .deckOptionId(deckOptionId)
                        .build()));
        Mockito.when(deckOptionService.findById(ArgumentMatchers.eq(deckOptionId)))
                .thenReturn(Mono.just(DeckOptionBO.builder().build()));
        Mockito.when(telegramClient.executeMethod(ArgumentMatchers.any(AnswerCallbackQueryMethod.class)))
                .thenReturn(Mono.empty());
        StepVerifier.create(handler.handle(update))
                .expectSubscription()
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void testFinishPollError() {
        final String callbackData = RandomStringUtils.randomAlphabetic(10);
        final String inlineMessageId = RandomStringUtils.randomAlphabetic(10);
        final long userId = RandomUtils.nextLong();
        final String callbackQueryId = RandomStringUtils.randomAlphabetic(10);
        setupUpdate(callbackData, inlineMessageId, userId, callbackQueryId, null, null, null);
        Mockito.when(keyboardMarkupService.getButtonType(ArgumentMatchers.eq(callbackData)))
                .thenReturn(Mono.just(ButtonType.FINISH));
        Mockito.when(pollLifecycleService.finishPoll(ArgumentMatchers.eq(inlineMessageId), ArgumentMatchers.eq(userId)))
                .thenReturn(Mono.error(IllegalAccessException::new));
        Mockito.when(telegramClient.executeMethod(ArgumentMatchers.any(AnswerCallbackQueryMethod.class)))
                .thenReturn(Mono.empty());
        StepVerifier.create(handler.handle(update))
                .expectSubscription()
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void testFinishPollSuccess() {
        final String callbackData = RandomStringUtils.randomAlphabetic(10);
        final String inlineMessageId = RandomStringUtils.randomAlphabetic(10);
        final long userId = RandomUtils.nextLong();
        final String callbackQueryId = RandomStringUtils.randomAlphabetic(10);
        setupUpdate(callbackData, inlineMessageId, userId, callbackQueryId, null, null, null);
        Mockito.when(keyboardMarkupService.getButtonType(ArgumentMatchers.eq(callbackData)))
                .thenReturn(Mono.just(ButtonType.FINISH));
        Mockito.when(pollLifecycleService.finishPoll(ArgumentMatchers.eq(inlineMessageId), ArgumentMatchers.eq(userId)))
                .thenReturn(Mono.empty());
        Mockito.when(telegramClient.executeMethod(ArgumentMatchers.any(AnswerCallbackQueryMethod.class)))
                .thenReturn(Mono.just(true));
        StepVerifier.create(handler.handle(update))
                .expectSubscription()
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void testRestartPollError() {
        final String callbackData = RandomStringUtils.randomAlphabetic(10);
        final String inlineMessageId = RandomStringUtils.randomAlphabetic(10);
        final long userId = RandomUtils.nextLong();
        final String callbackQueryId = RandomStringUtils.randomAlphabetic(10);
        setupUpdate(callbackData, inlineMessageId, userId, callbackQueryId, null, null, null);
        Mockito.when(keyboardMarkupService.getButtonType(ArgumentMatchers.eq(callbackData)))
                .thenReturn(Mono.just(ButtonType.RESTART));
        Mockito.when(pollLifecycleService.restartPoll(ArgumentMatchers.eq(inlineMessageId), ArgumentMatchers.eq(userId)))
                .thenReturn(Mono.error(IllegalAccessException::new));
        Mockito.when(telegramClient.executeMethod(ArgumentMatchers.any(AnswerCallbackQueryMethod.class)))
                .thenReturn(Mono.empty());
        StepVerifier.create(handler.handle(update))
                .expectSubscription()
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void testRestartPollSuccess() {
        final String callbackData = RandomStringUtils.randomAlphabetic(10);
        final String inlineMessageId = RandomStringUtils.randomAlphabetic(10);
        final long userId = RandomUtils.nextLong();
        final String callbackQueryId = RandomStringUtils.randomAlphabetic(10);
        setupUpdate(callbackData, inlineMessageId, userId, callbackQueryId, null, null, null);
        Mockito.when(keyboardMarkupService.getButtonType(ArgumentMatchers.eq(callbackData)))
                .thenReturn(Mono.just(ButtonType.RESTART));
        Mockito.when(pollLifecycleService.restartPoll(ArgumentMatchers.eq(inlineMessageId), ArgumentMatchers.eq(userId)))
                .thenReturn(Mono.empty());
        Mockito.when(telegramClient.executeMethod(ArgumentMatchers.any(AnswerCallbackQueryMethod.class)))
                .thenReturn(Mono.just(true));
        StepVerifier.create(handler.handle(update))
                .expectSubscription()
                .expectNext(true)
                .verifyComplete();
    }

    private void setupUpdate(final String callbackData,
                             final String inlineMessageId,
                             final Long userId,
                             final String callbackQueryId,
                             final String username,
                             final String firstName,
                             final String lastName) {
        Mockito.when(update.getCallbackQuery())
                .thenReturn(callbackQuery);
        Mockito.when(callbackQuery.getData())
                .thenReturn(callbackData);
        Mockito.when(callbackQuery.getInlineMessageId())
                .thenReturn(inlineMessageId);
        Mockito.when(callbackQuery.getId())
                .thenReturn(callbackQueryId);
        final User user = Mockito.mock(User.class);
        Mockito.when(callbackQuery.getFrom())
                .thenReturn(user);
        Mockito.when(user.getId())
                .thenReturn(userId);
        Mockito.lenient().when(user.getUsername())
                .thenReturn(username);
        Mockito.lenient().when(user.getFirstName())
                .thenReturn(firstName);
        Mockito.lenient().when(user.getLastName())
                .thenReturn(lastName);
    }

    @Test
    void testShouldHandle() {
        Mockito.when(update.getCallbackQuery())
                .thenReturn(CallbackQuery.builder()
                        .build());
        assertThat(handler.shouldHandle(update), CoreMatchers.is(true));
    }

}
