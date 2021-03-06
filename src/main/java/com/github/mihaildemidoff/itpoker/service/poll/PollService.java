package com.github.mihaildemidoff.itpoker.service.poll;

import com.github.mihaildemidoff.itpoker.mapper.PollMapper;
import com.github.mihaildemidoff.itpoker.model.bo.PollBO;
import com.github.mihaildemidoff.itpoker.model.common.PollStatus;
import com.github.mihaildemidoff.itpoker.model.common.ProcessingStatus;
import com.github.mihaildemidoff.itpoker.model.entity.PollEntity;
import com.github.mihaildemidoff.itpoker.model.exception.PollNotFoundException;
import com.github.mihaildemidoff.itpoker.repository.PollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final PollMapper pollMapper;

    @Transactional
    public Mono<PollBO> findNextPollForProcessing() {
        return pollRepository
                .findNextPollForProcessing()
                .map(poll -> poll.toBuilder()
                        .processingStatus(ProcessingStatus.PROCESSING)
                        .lastProcessingDate(LocalDateTime.now())
                        .needRefresh(false)
                        .build()
                )
                .flatMap(pollRepository::save)
                .map(pollMapper::toBO);
    }

    @Transactional
    public Mono<PollBO> moveToReadyToProcess(final Long id) {
        return pollRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new PollNotFoundException("Poll with id: " + id + " not found")))
                .map(poll -> poll.toBuilder()
                        .processingStatus(ProcessingStatus.READY_TO_PROCESS)
                        .build()
                )
                .flatMap(pollRepository::save)
                .map(pollMapper::toBO);
    }

    public Mono<Long> switchStuckRequestsToReadyToProcess(final Duration delta) {
        return pollRepository.updateStuckRequests(LocalDateTime.now().minus(delta));
    }

    public Mono<PollBO> findPollByMessageId(final String messageId) {
        return pollRepository
                .findByMessageId(messageId)
                .map(pollMapper::toBO);
    }

    @Transactional
    public Mono<Void> setNeedRefresh(final Long id) {
        return pollRepository
                .setNeedRefreshForPoll(id)
                .flatMap(updatedCount -> {
                    if (updatedCount != 1) {
                        return Mono.error(new PollNotFoundException("Didn't update need_refresh for poll: " + id));
                    }
                    return Mono.empty();
                });
    }

    @Transactional
    public Mono<Void> setPollStatusWithNeedRefresh(final Long id,
                                                   final PollStatus status) {
        return pollRepository
                .setStatusWithNeedRefresh(id, status)
                .flatMap(updatedCount -> {
                    if (updatedCount != 1) {
                        return Mono.error(new PollNotFoundException("Didn't update need_refresh for poll: " + id));
                    }
                    return Mono.empty();
                });
    }

    @Transactional
    public Mono<PollBO> createPoll(final Long deckId,
                                   final String messageId,
                                   final Long authorId,
                                   final String query) {
        return pollRepository.save(PollEntity.builder()
                        .deckId(deckId)
                        .messageId(messageId)
                        .authorId(authorId)
                        .query(query)
                        .needRefresh(true)
                        .processingStatus(ProcessingStatus.READY_TO_PROCESS)
                        .status(PollStatus.IN_PROGRESS)
                        .build())
                .map(pollMapper::toBO);
    }
}
