package com.github.mihaildemidoff.itpoker.service.deck;

import com.github.mihaildemidoff.itpoker.mapper.DeckMapper;
import com.github.mihaildemidoff.itpoker.model.bo.DeckBO;
import com.github.mihaildemidoff.itpoker.repository.DeckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class DeckService {

    private final DeckRepository deckRepository;
    private final DeckMapper deckMapper;

    @Transactional
    public Flux<DeckBO> findAllDecks() {
        return deckRepository
                .findAll()
                .map(deckMapper::toBO);
    }

}
