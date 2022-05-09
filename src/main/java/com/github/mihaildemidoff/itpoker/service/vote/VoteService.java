package com.github.mihaildemidoff.itpoker.service.vote;

import com.github.mihaildemidoff.itpoker.mapper.VoteMapper;
import com.github.mihaildemidoff.itpoker.model.bo.VoteBO;
import com.github.mihaildemidoff.itpoker.model.common.PollStatus;
import com.github.mihaildemidoff.itpoker.model.entity.VoteEntity;
import com.github.mihaildemidoff.itpoker.model.exception.PollAlreadyFinishedException;
import com.github.mihaildemidoff.itpoker.model.exception.PollNotFoundException;
import com.github.mihaildemidoff.itpoker.repository.VoteRepository;
import com.github.mihaildemidoff.itpoker.service.poll.PollService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final VoteMapper voteMapper;
    private final PollService pollService;

    public Mono<VoteBO> createOrUpdateVote(final String pollMessageId,
                                           final Long deckOptionId,
                                           final Long userId,
                                           final String username,
                                           final String firstName,
                                           final String lastName) {
        return pollService.findPollByMessageId(pollMessageId)
                .flatMap(poll -> {
                    if (poll.status() == PollStatus.FINISHED) {
                        return Mono.error(new PollAlreadyFinishedException("Poll already finished"));
                    }
                    return Mono.just(poll);
                })
                .flatMap(poll -> pollService.setNeedUpdate(poll.id()))
                .switchIfEmpty(Mono.error(new PollNotFoundException("Poll with messageId " + pollMessageId + " Not found")))
                .flatMap(poll -> voteRepository
                        .findByPollIdAndUserId(poll.id(), userId)
                        .map(vote -> vote
                                .withDeckOptionId(deckOptionId)
                                .withFirstName(firstName)
                                .withLastName(lastName)
                                .withUsername(username))
                        .switchIfEmpty(Mono.fromCallable(() -> {
                                    final VoteEntity vote = new VoteEntity();
                                    vote.setPollId(poll.id());
                                    vote.setDeckOptionId(deckOptionId);
                                    vote.setUserId(userId);
                                    vote.setFirstName(firstName);
                                    vote.setLastName(lastName);
                                    vote.setUsername(username);
                                    return vote;
                                })
                        )
                        .flatMap(voteRepository::save))
                .map(voteMapper::toBO);
    }

    public Flux<VoteBO> findVotesByPollId(final Long pollId) {
        return voteRepository
                .findByPollId(pollId)
                .map(voteMapper::toBO);
    }

    public Mono<Void> deleteVotesForPoll(final Long pollId) {
        return voteRepository.deleteAllByPollId(pollId);
    }
}
