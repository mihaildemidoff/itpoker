package com.github.mihaildemidoff.itpoker.service.poll;

import com.github.mihaildemidoff.itpoker.mapper.PollMapper;
import com.github.mihaildemidoff.itpoker.model.bo.PollBO;
import com.github.mihaildemidoff.itpoker.model.common.PollStatus;
import com.github.mihaildemidoff.itpoker.model.common.ProcessingStatus;
import com.github.mihaildemidoff.itpoker.model.entity.PollEntity;
import com.github.mihaildemidoff.itpoker.model.exception.PollNotFoundException;
import com.github.mihaildemidoff.itpoker.repository.PollRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

@ExtendWith(MockitoExtension.class)
class PollServiceTest {
    @InjectMocks
    private PollService pollService;
    @Mock
    private PollRepository pollRepository;
    @Mock
    private PollMapper pollMapper;

    @Test
    void testFindNextPollForProcessing() {
        Mockito.when(pollRepository.findNextPollForProcessing())
                .thenReturn(Mono.just(PollEntity.builder()
                        .processingStatus(ProcessingStatus.READY_TO_PROCESS)
                        .needRefresh(true)
                        .build()));
        Mockito.when(pollRepository.save(ArgumentMatchers.argThat(arg -> arg.getProcessingStatus() == ProcessingStatus.PROCESSING && !arg.getNeedRefresh()))).thenAnswer((Answer<Mono<PollEntity>>) invocationOnMock -> Mono.just(invocationOnMock.getArgument(0)));
        final PollBO expected = PollBO.builder().build();
        Mockito.when(pollMapper.toBO(ArgumentMatchers.argThat(arg -> arg.getProcessingStatus() == ProcessingStatus.PROCESSING && !arg.getNeedRefresh()))).thenReturn(expected);
        StepVerifier.create(pollService.findNextPollForProcessing())
                .expectSubscription()
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void testMoveToReadyToProcess() {
        final Long pollId = RandomUtils.nextLong();
        Mockito.when(pollRepository.findById(ArgumentMatchers.eq(pollId)))
                .thenReturn(Mono.just(PollEntity.builder()
                        .processingStatus(ProcessingStatus.PROCESSING)
                        .build()));
        Mockito.when(pollRepository.save(ArgumentMatchers.argThat(arg -> arg.getProcessingStatus() == ProcessingStatus.READY_TO_PROCESS))).thenAnswer((Answer<Mono<PollEntity>>) invocationOnMock -> Mono.just(invocationOnMock.getArgument(0)));
        final PollBO expected = PollBO.builder().build();
        Mockito.when(pollMapper.toBO(ArgumentMatchers.argThat(arg -> arg.getProcessingStatus() == ProcessingStatus.READY_TO_PROCESS))).thenReturn(expected);
        StepVerifier.create(pollService.moveToReadyToProcess(pollId))
                .expectSubscription()
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void testMoveToReadyToProcessPollNotFound() {
        final Long pollId = RandomUtils.nextLong();
        Mockito.when(pollRepository.findById(ArgumentMatchers.eq(pollId)))
                .thenReturn(Mono.empty());
        StepVerifier.create(pollService.moveToReadyToProcess(pollId))
                .expectSubscription()
                .expectError(PollNotFoundException.class)
                .verify();
    }

    @Test
    void testFindPollByMessageId() {
        final String messageId = RandomStringUtils.randomAlphabetic(10);
        final PollEntity poll = PollEntity.builder().build();
        Mockito.when(pollRepository.findByMessageId(ArgumentMatchers.eq(messageId)))
                .thenReturn(Mono.just(poll));
        final PollBO expected = PollBO.builder().build();
        Mockito.when(pollMapper.toBO(ArgumentMatchers.eq(poll)))
                .thenReturn(expected);
        StepVerifier.create(pollService.findPollByMessageId(messageId))
                .expectSubscription()
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void testSetPollStatusWithNeedRefresh() {
        final Long pollId = RandomUtils.nextLong();
        final PollStatus status = PollStatus.values()[RandomUtils.nextInt(0, PollStatus.values().length)];
        Mockito.when(pollRepository.setStatusWithNeedRefresh(ArgumentMatchers.eq(pollId), ArgumentMatchers.eq(status)))
                .thenReturn(Mono.just(1L));
        StepVerifier.create(pollService.setPollStatusWithNeedRefresh(pollId, status))
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
        Mockito.verify(pollRepository, Mockito.times(1))
                .setStatusWithNeedRefresh(ArgumentMatchers.eq(pollId), ArgumentMatchers.eq(status));
    }

    @Test
    void testSetPollStatusWithNeedRefreshPollNotFound() {
        final Long pollId = RandomUtils.nextLong();
        final PollStatus status = PollStatus.values()[RandomUtils.nextInt(0, PollStatus.values().length)];
        Mockito.when(pollRepository.setStatusWithNeedRefresh(ArgumentMatchers.eq(pollId), ArgumentMatchers.eq(status)))
                .thenReturn(Mono.just(0L));
        StepVerifier.create(pollService.setPollStatusWithNeedRefresh(pollId, status))
                .expectSubscription()
                .expectError(PollNotFoundException.class)
                .verify();
    }

    @Test
    void testSetNeedRefresh() {
        final Long pollId = RandomUtils.nextLong();
        Mockito.when(pollRepository.setNeedRefreshForPoll(ArgumentMatchers.eq(pollId)))
                .thenReturn(Mono.just(1L));
        StepVerifier.create(pollService.setNeedRefresh(pollId))
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
        Mockito.verify(pollRepository, Mockito.times(1))
                .setNeedRefreshForPoll(ArgumentMatchers.eq(pollId));
    }

    @Test
    void testSetNeedRefreshPollNotFound() {
        final Long pollId = RandomUtils.nextLong();
        Mockito.when(pollRepository.setNeedRefreshForPoll(ArgumentMatchers.eq(pollId)))
                .thenReturn(Mono.just(0L));
        StepVerifier.create(pollService.setNeedRefresh(pollId))
                .expectSubscription()
                .expectError(PollNotFoundException.class)
                .verify();
    }

    @Test
    void testCreate() {
        final Long deckId = RandomUtils.nextLong();
        final String messageId = RandomStringUtils.randomAlphabetic(10);
        final Long authorId = RandomUtils.nextLong();
        final String query = RandomStringUtils.randomAlphabetic(10);
        final PollEntityArgumentMatcher pollMatcher = new PollEntityArgumentMatcher(deckId, messageId, authorId, query);
        Mockito.when(pollRepository.save(ArgumentMatchers.argThat(pollMatcher)))
                .thenAnswer((Answer<Mono<PollEntity>>) invocationOnMock -> Mono.just(invocationOnMock.getArgument(0)));
        final PollBO expected = PollBO.builder().build();
        Mockito.when(pollMapper.toBO(ArgumentMatchers.argThat(pollMatcher)))
                .thenReturn(expected);
        StepVerifier.create(pollService.createPoll(deckId, messageId, authorId, query))
                .expectSubscription()
                .expectNext(expected)
                .verifyComplete();
    }

    private record PollEntityArgumentMatcher(Long deckId, String messageId, Long authorId,
                                             String query) implements ArgumentMatcher<PollEntity> {

        @Override
        public boolean matches(final PollEntity arg) {
            return Objects.equals(arg.getDeckId(), deckId)
                    && Objects.equals(arg.getMessageId(), messageId)
                    && Objects.equals(arg.getAuthorId(), authorId)
                    && Objects.equals(arg.getQuery(), query)
                    && Objects.equals(arg.getNeedRefresh(), true)
                    && arg.getProcessingStatus() == ProcessingStatus.READY_TO_PROCESS
                    && arg.getStatus() == PollStatus.IN_PROGRESS;
        }
    }
}
