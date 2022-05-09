package com.github.mihaildemidoff.itpoker.service.poll;

import com.github.mihaildemidoff.itpoker.mapper.PollMapper;
import com.github.mihaildemidoff.itpoker.model.bo.PollBO;
import com.github.mihaildemidoff.itpoker.model.entity.PollEntity;
import com.github.mihaildemidoff.itpoker.model.common.ProcessingStatus;
import com.github.mihaildemidoff.itpoker.repository.PollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final PollMapper pollMapper;

    @Transactional
    public Mono<PollBO> findNextPollForProcessing() {
        return pollRepository
                .findNextPollForProcessing()
                .map(poll -> poll.withProcessingStatus(ProcessingStatus.PROCESSING)
                        .withNeedRefresh(false))
                .flatMap(pollRepository::save)
                .map(pollMapper::toBO);
    }

    @Transactional
    public Mono<PollBO> moveToReadyToProcess(final Long id) {
        return pollRepository
                .findById(id)
                .map(poll -> poll.withProcessingStatus(ProcessingStatus.READY_TO_PROCESS))
                .flatMap(pollRepository::save)
                .map(pollMapper::toBO);
    }

    public Mono<PollBO> findPollByMessageId(final String messageId) {
        return pollRepository
                .findByMessageId(messageId)
                .map(pollMapper::toBO);
    }

    public Mono<PollBO> setNeedUpdate(final Long id) {
        return pollRepository
                .findById(id)
                .map(poll -> poll.withNeedRefresh(true))
                .flatMap(pollRepository::save)
                .map(pollMapper::toBO);
    }

    @Transactional
    public Mono<PollBO> createPoll(final Long deckId,
                                   final String messageId,
                                   final Long authorId,
                                   final String query) {
        final PollEntity poll = new PollEntity();
        poll.setDeckId(deckId);
        poll.setMessageId(messageId);
        poll.setAuthorId(authorId);
        poll.setQuery(query);
        poll.setNeedRefresh(true);
        poll.setProcessingStatus(ProcessingStatus.READY_TO_PROCESS);
        return pollRepository.save(poll)
                .map(pollMapper::toBO);
    }
}
