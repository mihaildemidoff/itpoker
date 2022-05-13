package com.github.mihaildemidoff.itpoker.repository;

import com.github.mihaildemidoff.itpoker.model.common.PollStatus;
import com.github.mihaildemidoff.itpoker.model.entity.PollEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface PollRepository extends ReactiveCrudRepository<PollEntity, Long> {

    @Modifying
    @Query("""
            UPDATE poll
            SET processing_status = 'READY_TO_PROCESS'
            WHERE processing_status = 'PROCESSING'
              AND last_processing_date < :fromDate"""
    )
    Mono<Long> updateStuckRequests(LocalDateTime fromDate);

    Mono<PollEntity> findByMessageId(String messageId);

    @Query(value = """
            SELECT *
            FROM poll
            WHERE need_refresh = true
              AND processing_status = 'READY_TO_PROCESS' FOR UPDATE SKIP LOCKED
            limit 1"""
    )
    Mono<PollEntity> findNextPollForProcessing();

    @Modifying
    @Query(value = """
            UPDATE poll
            SET need_refresh = true
            WHERE id = :pollId"""
    )
    Mono<Long> setNeedRefreshForPoll(Long pollId);

    @Modifying
    @Query(value = """
            UPDATE poll
            SET need_refresh = true, status = :status
            WHERE id = :pollId"""
    )
    Mono<Long> setStatusWithNeedRefresh(Long pollId, PollStatus status);


}
