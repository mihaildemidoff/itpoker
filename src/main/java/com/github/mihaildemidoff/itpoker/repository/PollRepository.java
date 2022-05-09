package com.github.mihaildemidoff.itpoker.repository;

import com.github.mihaildemidoff.itpoker.model.entity.PollEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface PollRepository extends ReactiveCrudRepository<PollEntity, Long> {

    Mono<PollEntity> findByMessageId(String messageId);

    @Query(value = """
            SELECT *
            FROM poll
            WHERE need_refresh = true
              AND processing_status = 'READY_TO_PROCESS' FOR UPDATE SKIP LOCKED
            limit 1"""
    )
    Mono<PollEntity> findNextPollForProcessing();

}
