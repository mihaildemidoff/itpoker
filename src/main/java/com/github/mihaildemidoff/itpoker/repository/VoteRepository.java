package com.github.mihaildemidoff.itpoker.repository;

import com.github.mihaildemidoff.itpoker.model.entity.VoteEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VoteRepository extends ReactiveCrudRepository<VoteEntity, Long> {

    Mono<VoteEntity> findByPollIdAndUserId(Long pollId,
                                           Long userId);

    Flux<VoteEntity> findByPollId(Long pollId);

    Mono<Void> deleteAllByPollId(Long pollId);

}
