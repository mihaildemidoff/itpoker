package com.github.mihaildemidoff.itpoker.service.deck;

import com.github.mihaildemidoff.itpoker.mapper.DeckMapper;
import com.github.mihaildemidoff.itpoker.model.bo.DeckBO;
import com.github.mihaildemidoff.itpoker.model.entity.DeckEntity;
import com.github.mihaildemidoff.itpoker.repository.DeckRepository;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class DeckServiceTest {

    @InjectMocks
    private DeckService deckService;
    @Mock
    private DeckRepository deckRepository;
    @Mock
    private DeckMapper deckMapper;

    @Test
    void testFindAll() {
        final long numberOfDecks = RandomUtils.nextLong(1, 10);
        final List<DeckEntity> decks = Stream.generate(() -> DeckEntity.builder().build())
                .limit(numberOfDecks)
                .toList();
        Mockito.when(deckRepository.findAll())
                .thenReturn(Flux.fromIterable(decks));
        Mockito.when(deckMapper.toBO(ArgumentMatchers.any(DeckEntity.class)))
                .thenReturn(DeckBO.builder().build());
        StepVerifier.create(deckService.findAllDecks())
                .expectSubscription()
                .expectNextCount(numberOfDecks)
                .verifyComplete();
    }

}
