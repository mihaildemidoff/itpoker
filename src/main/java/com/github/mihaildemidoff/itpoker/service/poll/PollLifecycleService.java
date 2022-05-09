package com.github.mihaildemidoff.itpoker.service.poll;

import com.github.mihaildemidoff.itpoker.mapper.PollMapper;
import com.github.mihaildemidoff.itpoker.model.bo.PollBO;
import com.github.mihaildemidoff.itpoker.model.common.PollStatus;
import com.github.mihaildemidoff.itpoker.model.entity.PollEntity;
import com.github.mihaildemidoff.itpoker.model.exception.PollNotFoundException;
import com.github.mihaildemidoff.itpoker.model.exception.UserNotAllowedException;
import com.github.mihaildemidoff.itpoker.repository.PollRepository;
import com.github.mihaildemidoff.itpoker.service.vote.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PollLifecycleService {
    private final PollRepository pollRepository;
    private final PollMapper pollMapper;
    private final VoteService voteService;

    public Mono<PollBO> finishPoll(final String messageId,
                                   final Long userId) {
        return pollRepository
                .findByMessageId(messageId)
                .switchIfEmpty(Mono.error(new PollNotFoundException()))
                .flatMap(poll -> {
                    if (!Objects.equals(poll.getAuthorId(), userId)) {
                        return Mono.error(new UserNotAllowedException());
                    } else {
                        final PollEntity updatedPoll = poll.toBuilder()
                                .status(PollStatus.FINISHED)
                                .needRefresh(true)
                                .build();
                        return pollRepository.save(updatedPoll);
                    }
                })
                .map(pollMapper::toBO);
    }

    public Mono<PollBO> restartPoll(final String messageId,
                                    final Long userId) {
        return pollRepository
                .findByMessageId(messageId)
                .switchIfEmpty(Mono.error(new PollNotFoundException()))
                .flatMap(poll -> {
                    if (!Objects.equals(poll.getAuthorId(), userId)) {
                        return Mono.error(new UserNotAllowedException());
                    } else {
                        final PollEntity updatedPoll = poll.toBuilder()
                                .status(PollStatus.IN_PROGRESS)
                                .needRefresh(true)
                                .build();
                        return voteService
                                .deleteVotesForPoll(poll.getId())
                                .then(pollRepository.save(updatedPoll));
                    }
                })
                .map(pollMapper::toBO);
    }
}
