package com.github.mihaildemidoff.itpoker.service.vote;

import com.github.mihaildemidoff.itpoker.mapper.VoteMapper;
import com.github.mihaildemidoff.itpoker.model.bo.PollBO;
import com.github.mihaildemidoff.itpoker.model.bo.VoteBO;
import com.github.mihaildemidoff.itpoker.model.common.PollStatus;
import com.github.mihaildemidoff.itpoker.model.entity.VoteEntity;
import com.github.mihaildemidoff.itpoker.model.exception.PollAlreadyFinishedException;
import com.github.mihaildemidoff.itpoker.model.exception.PollNotFoundException;
import com.github.mihaildemidoff.itpoker.repository.VoteRepository;
import com.github.mihaildemidoff.itpoker.service.poll.PollService;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @InjectMocks
    private VoteService voteService;
    @Mock
    private VoteRepository voteRepository;
    @Mock
    private VoteMapper voteMapper;
    @Mock
    private PollService pollService;

    @Test
    void testCreateOrUpdateVoteAlreadyExist() {
        final String pollMessageId = RandomStringUtils.randomAlphabetic(10);
        final Long deckOptionId = RandomUtils.nextLong();
        final Long userId = RandomUtils.nextLong();
        final String username = RandomStringUtils.randomAlphabetic(10);
        final String firstname = RandomStringUtils.randomAlphabetic(10);
        final String lastname = RandomStringUtils.randomAlphabetic(10);
        final Long pollId = RandomUtils.nextLong();
        final PollBO poll = PollBO.builder()
                .id(pollId)
                .status(PollStatus.IN_PROGRESS)
                .build();
        Mockito.when(pollService.findPollByMessageId(ArgumentMatchers.eq(pollMessageId)))
                .thenReturn(Mono.just(poll));
        Mockito.when(pollService.setNeedRefresh(ArgumentMatchers.eq(pollId)))
                .thenReturn(Mono.empty());
        final VoteEntity existingVote = VoteEntity.builder()
                .id(RandomUtils.nextLong())
                .pollId(pollId)
                .deckOptionId(RandomUtils.nextLong())
                .userId(RandomUtils.nextLong())
                .firstName(RandomStringUtils.randomAlphabetic(10))
                .lastName(RandomStringUtils.randomAlphabetic(10))
                .username(RandomStringUtils.randomAlphabetic(10))
                .createdDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(RandomUtils.nextLong()), ZoneId.systemDefault()))
                .modifiedDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(RandomUtils.nextLong()), ZoneId.systemDefault()))
                .build();
        Mockito.when(voteRepository.findByPollIdAndUserId(ArgumentMatchers.eq(pollId), ArgumentMatchers.eq(userId)))
                .thenReturn(Mono.just(existingVote));
        final VoteEntityArgumentMatcher voteMatcher = new VoteEntityArgumentMatcher(existingVote.getId(), pollId, deckOptionId, userId, firstname, lastname, username, existingVote.getCreatedDate(), existingVote.getModifiedDate());
        Mockito.when(voteRepository.save(ArgumentMatchers.argThat(voteMatcher)))
                .thenAnswer((Answer<Mono<VoteEntity>>) invocationOnMock -> Mono.just(invocationOnMock.getArgument(0)));
        final VoteBO expected = VoteBO.builder().build();
        Mockito.when(voteMapper.toBO(ArgumentMatchers.argThat(voteMatcher)))
                .thenReturn(expected);
        StepVerifier.create(voteService.createOrUpdateVote(pollMessageId, deckOptionId, userId, username, firstname, lastname))
                .expectSubscription()
                .expectNext(expected)
                .verifyComplete();
    }


    @Test
    void testCreateOrUpdateVoteNotExist() {
        final String pollMessageId = RandomStringUtils.randomAlphabetic(10);
        final Long deckOptionId = RandomUtils.nextLong();
        final Long userId = RandomUtils.nextLong();
        final String username = RandomStringUtils.randomAlphabetic(10);
        final String firstname = RandomStringUtils.randomAlphabetic(10);
        final String lastname = RandomStringUtils.randomAlphabetic(10);
        final Long pollId = RandomUtils.nextLong();
        final PollBO poll = PollBO.builder()
                .id(pollId)
                .status(PollStatus.IN_PROGRESS)
                .build();
        Mockito.when(pollService.findPollByMessageId(ArgumentMatchers.eq(pollMessageId)))
                .thenReturn(Mono.just(poll));
        Mockito.when(pollService.setNeedRefresh(ArgumentMatchers.eq(pollId)))
                .thenReturn(Mono.empty());
        Mockito.when(voteRepository.findByPollIdAndUserId(ArgumentMatchers.eq(pollId), ArgumentMatchers.eq(userId)))
                .thenReturn(Mono.empty());
        final VoteEntityArgumentMatcher voteMatcher = new VoteEntityArgumentMatcher(null, pollId, deckOptionId, userId, firstname, lastname, username, null, null);
        Mockito.when(voteRepository.save(ArgumentMatchers.argThat(voteMatcher)))
                .thenAnswer((Answer<Mono<VoteEntity>>) invocationOnMock -> Mono.just(invocationOnMock.getArgument(0)));
        final VoteBO expected = VoteBO.builder().build();
        Mockito.when(voteMapper.toBO(ArgumentMatchers.argThat(voteMatcher)))
                .thenReturn(expected);
        StepVerifier.create(voteService.createOrUpdateVote(pollMessageId, deckOptionId, userId, username, firstname, lastname))
                .expectSubscription()
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void testCreateOrUpdateNeedRefreshUpdateNotSuccess() {
        final String pollMessageId = RandomStringUtils.randomAlphabetic(10);
        final Long pollId = RandomUtils.nextLong();
        final PollBO poll = PollBO.builder()
                .id(pollId)
                .status(PollStatus.IN_PROGRESS)
                .build();
        Mockito.when(pollService.findPollByMessageId(ArgumentMatchers.eq(pollMessageId)))
                .thenReturn(Mono.just(poll));
        Mockito.when(pollService.setNeedRefresh(ArgumentMatchers.eq(pollId)))
                .thenReturn(Mono.error(new PollNotFoundException()));
        StepVerifier.create(voteService.createOrUpdateVote(pollMessageId, null, null, null, null, null))
                .expectSubscription()
                .expectError(PollNotFoundException.class)
                .verify();
    }

    @Test
    void testCreateOrUpdateVotePollFinished() {
        final String pollMessageId = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(pollService.findPollByMessageId(ArgumentMatchers.eq(pollMessageId)))
                .thenReturn(Mono.just(PollBO.builder()
                        .status(PollStatus.FINISHED)
                        .build()));
        StepVerifier.create(voteService.createOrUpdateVote(pollMessageId, null, null, null, null, null))
                .expectSubscription()
                .expectError(PollAlreadyFinishedException.class)
                .verify();
    }

    @Test
    void testCreateOrUpdateVotePollNotFound() {
        final String pollMessageId = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(pollService.findPollByMessageId(ArgumentMatchers.eq(pollMessageId)))
                .thenReturn(Mono.empty());
        StepVerifier.create(voteService.createOrUpdateVote(pollMessageId, null, null, null, null, null))
                .expectSubscription()
                .expectError(PollNotFoundException.class)
                .verify();
    }


    @Test
    void testFindVoteByPollId() {
        final Long pollId = RandomUtils.nextLong();
        final long numberOfVotes = RandomUtils.nextLong(1, 10);
        final List<VoteEntity> votes = Stream.generate(() -> VoteEntity.builder().build())
                .limit(numberOfVotes)
                .toList();
        Mockito.when(voteRepository.findByPollId(ArgumentMatchers.eq(pollId)))
                .thenReturn(Flux.fromIterable(votes));
        Mockito.when(voteMapper.toBO(ArgumentMatchers.any(VoteEntity.class)))
                .thenReturn(VoteBO.builder().build());
        StepVerifier.create(voteService.findVotesByPollId(pollId))
                .expectSubscription()
                .expectNextCount(numberOfVotes)
                .verifyComplete();
    }

    @Test
    void testDeleteVotesForPoll() {
        final Long pollId = RandomUtils.nextLong();
        Mockito.when(voteRepository.deleteAllByPollId(ArgumentMatchers.eq(pollId)))
                .thenReturn(Mono.empty());
        StepVerifier.create(voteService.deleteVotesForPoll(pollId))
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
        Mockito.verify(voteRepository, Mockito.times(1))
                .deleteAllByPollId(ArgumentMatchers.eq(pollId));
    }

    private record VoteEntityArgumentMatcher(Long id,
                                             Long pollId,
                                             Long deckOptionId,
                                             Long userId,
                                             String firstname,
                                             String lastname,
                                             String username,
                                             LocalDateTime createdDate,
                                             LocalDateTime modifiedDate) implements ArgumentMatcher<VoteEntity> {
        @Override
        public boolean matches(final VoteEntity arg) {
            return Objects.equals(arg.getId(), id)
                    && Objects.equals(arg.getPollId(), pollId)
                    && Objects.equals(arg.getDeckOptionId(), deckOptionId)
                    && Objects.equals(arg.getUserId(), userId)
                    && Objects.equals(arg.getFirstName(), firstname)
                    && Objects.equals(arg.getLastName(), lastname)
                    && Objects.equals(arg.getUsername(), username)
                    && Objects.equals(arg.getCreatedDate(), createdDate)
                    && Objects.equals(arg.getModifiedDate(), modifiedDate);
        }
    }
}
