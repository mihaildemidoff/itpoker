package com.github.mihaildemidoff.itpoker.service.poll;

import com.github.mihaildemidoff.itpoker.model.bo.PollBO;
import com.github.mihaildemidoff.itpoker.model.common.PollStatus;
import com.github.mihaildemidoff.itpoker.model.exception.PollNotFoundException;
import com.github.mihaildemidoff.itpoker.model.exception.UserNotAllowedException;
import com.github.mihaildemidoff.itpoker.service.vote.VoteService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class PollLifecycleServiceTest {

    @InjectMocks
    private PollLifecycleService pollLifecycleService;
    @Mock
    private PollService pollService;
    @Mock
    private VoteService voteService;

    @Test
    void testRestartPollNotFound() {
        final String messageId = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(pollService.findPollByMessageId(ArgumentMatchers.eq(messageId)))
                .thenReturn(Mono.empty());
        StepVerifier.create(pollLifecycleService.restartPoll(messageId, RandomUtils.nextLong()))
                .expectSubscription()
                .verifyError(PollNotFoundException.class);
    }

    @Test
    void testRestartPollIncorrectUser() {
        final String messageId = RandomStringUtils.randomAlphabetic(10);
        final Long userId = RandomUtils.nextLong();
        final PollBO foundPoll = PollBO.builder()
                .authorId(RandomUtils.nextLong())
                .build();
        Mockito.when(pollService.findPollByMessageId(ArgumentMatchers.eq(messageId)))
                .thenReturn(Mono.just(foundPoll));
        StepVerifier.create(pollLifecycleService.restartPoll(messageId, userId))
                .expectSubscription()
                .verifyError(UserNotAllowedException.class);
    }

    @Test
    void testRestartPoll() {
        final String messageId = RandomStringUtils.randomAlphabetic(10);
        final Long userId = RandomUtils.nextLong();
        final Long pollId = RandomUtils.nextLong();
        final PollBO foundPoll = PollBO.builder()
                .id(pollId)
                .authorId(userId)
                .build();
        Mockito.when(pollService.findPollByMessageId(ArgumentMatchers.eq(messageId)))
                .thenReturn(Mono.just(foundPoll));
        Mockito.when(voteService.deleteVotesForPoll(ArgumentMatchers.eq(pollId)))
                .thenReturn(Mono.empty());
        Mockito.when(pollService.setPollStatusWithNeedRefresh(ArgumentMatchers.eq(pollId), ArgumentMatchers.eq(PollStatus.IN_PROGRESS)))
                .thenReturn(Mono.empty());
        StepVerifier.create(pollLifecycleService.restartPoll(messageId, userId))
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
        Mockito.verify(pollService, Mockito.times(1))
                .setPollStatusWithNeedRefresh(ArgumentMatchers.eq(pollId), ArgumentMatchers.eq(PollStatus.IN_PROGRESS));
        Mockito.verify(voteService, Mockito.times(1))
                .deleteVotesForPoll(ArgumentMatchers.eq(pollId));
    }

    @Test
    void testFinishPollNotFound() {
        final String messageId = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(pollService.findPollByMessageId(ArgumentMatchers.eq(messageId)))
                .thenReturn(Mono.empty());
        StepVerifier.create(pollLifecycleService.finishPoll(messageId, RandomUtils.nextLong()))
                .expectSubscription()
                .verifyError(PollNotFoundException.class);
    }

    @Test
    void testFinishPollIncorrectUser() {
        final String messageId = RandomStringUtils.randomAlphabetic(10);
        final Long userId = RandomUtils.nextLong();
        final PollBO foundPoll = PollBO.builder()
                .authorId(RandomUtils.nextLong())
                .build();
        Mockito.when(pollService.findPollByMessageId(ArgumentMatchers.eq(messageId)))
                .thenReturn(Mono.just(foundPoll));
        StepVerifier.create(pollLifecycleService.finishPoll(messageId, userId))
                .expectSubscription()
                .verifyError(UserNotAllowedException.class);
    }

    @Test
    void testFinishPoll() {
        final String messageId = RandomStringUtils.randomAlphabetic(10);
        final Long userId = RandomUtils.nextLong();
        final Long pollId = RandomUtils.nextLong();
        final PollBO foundPoll = PollBO.builder()
                .id(pollId)
                .authorId(userId)
                .build();
        Mockito.when(pollService.findPollByMessageId(ArgumentMatchers.eq(messageId)))
                .thenReturn(Mono.just(foundPoll));
        Mockito.when(pollService.setPollStatusWithNeedRefresh(ArgumentMatchers.eq(pollId), ArgumentMatchers.eq(PollStatus.FINISHED)))
                .thenReturn(Mono.empty());
        StepVerifier.create(pollLifecycleService.finishPoll(messageId, userId))
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
        Mockito.verify(pollService, Mockito.times(1))
                .setPollStatusWithNeedRefresh(ArgumentMatchers.eq(pollId), ArgumentMatchers.eq(PollStatus.FINISHED));
    }

}
