package com.github.mihaildemidoff.itpoker.repository;

import com.github.mihaildemidoff.itpoker.model.entity.DeckEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface DeckRepository extends ReactiveCrudRepository<DeckEntity, Long> {
}
