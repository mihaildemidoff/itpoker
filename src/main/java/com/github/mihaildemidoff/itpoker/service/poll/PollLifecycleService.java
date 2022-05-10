package com.github.mihaildemidoff.itpoker.service.poll;

import com.github.mihaildemidoff.itpoker.model.common.PollStatus;
import com.github.mihaildemidoff.itpoker.model.exception.PollNotFoundException;
import com.github.mihaildemidoff.itpoker.model.exception.UserNotAllowedException;
import com.github.mihaildemidoff.itpoker.service.vote.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PollLifecycleService {
    private final PollService pollService;
    private final VoteService voteService;

    @Transactional
    public Mono<Void> finishPoll(final String messageId,
                                 final Long userId) {
        return pollService
                .findPollByMessageId(messageId)
                .switchIfEmpty(Mono.error(new PollNotFoundException()))
                .flatMap(poll -> {
                    if (!Objects.equals(poll.authorId(), userId)) {
                        return Mono.error(new UserNotAllowedException());
                    } else {
                        return pollService.setPollStatusWithNeedRefresh(poll.id(), PollStatus.FINISHED);
                    }
                });
    }

    @Transactional
    public Mono<Void> restartPoll(final String messageId,
                                  final Long userId) {
        return pollService
                .findPollByMessageId(messageId)
                .switchIfEmpty(Mono.error(new PollNotFoundException()))
                .flatMap(poll -> {
                    if (!Objects.equals(poll.authorId(), userId)) {
                        return Mono.error(new UserNotAllowedException());
                    } else {
                        return voteService.deleteVotesForPoll(poll.id())
                                .then(pollService.setPollStatusWithNeedRefresh(poll.id(), PollStatus.IN_PROGRESS));
                    }
                });
    }
}
