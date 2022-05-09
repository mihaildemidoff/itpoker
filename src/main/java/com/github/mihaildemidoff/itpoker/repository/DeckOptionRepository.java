package com.github.mihaildemidoff.itpoker.repository;

import com.github.mihaildemidoff.itpoker.model.entity.DeckOptionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface DeckOptionRepository extends ReactiveCrudRepository<DeckOptionEntity, Long> {
    Flux<DeckOptionEntity> findAllByDeckIdOrderByRowAscIndexAsc(Long deckId);
}
