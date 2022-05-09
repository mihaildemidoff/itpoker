package com.github.mihaildemidoff.itpoker.service.deck;

import com.github.mihaildemidoff.itpoker.mapper.DeckOptionMapper;
import com.github.mihaildemidoff.itpoker.model.bo.DeckOptionBO;
import com.github.mihaildemidoff.itpoker.repository.DeckOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeckOptionService {
    private final DeckOptionRepository deckOptionRepository;
    private final DeckOptionMapper deckOptionMapper;

    public Flux<DeckOptionBO> findAllForDeck(final Long deckId) {
        return deckOptionRepository.findAllByDeckIdOrderByRowAscIndexAsc(deckId)
                .map(deckOptionMapper::toBO);
    }

    public Mono<DeckOptionBO> findById(final Long id) {
        return deckOptionRepository
                .findById(id)
                .map(deckOptionMapper::toBO);
    }

    public Flux<DeckOptionBO> findById(final List<Long> ids) {
        return deckOptionRepository
                .findAllById(ids)
                .map(deckOptionMapper::toBO);
    }

}
