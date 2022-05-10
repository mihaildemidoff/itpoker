package com.github.mihaildemidoff.itpoker.service.deck;

import com.github.mihaildemidoff.itpoker.mapper.DeckOptionMapper;
import com.github.mihaildemidoff.itpoker.model.bo.DeckOptionBO;
import com.github.mihaildemidoff.itpoker.model.entity.DeckOptionEntity;
import com.github.mihaildemidoff.itpoker.repository.DeckOptionRepository;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class DeckOptionServiceTest {

    @InjectMocks
    private DeckOptionService deckOptionService;
    @Mock
    private DeckOptionRepository deckOptionRepository;
    @Mock
    private DeckOptionMapper deckOptionMapper;

    @Test
    void testFindAllForDeck() {
        final Long deckId = RandomUtils.nextLong();
        final long numberOfOptions = RandomUtils.nextLong(1, 10);
        final List<DeckOptionEntity> options = Stream.generate(() -> DeckOptionEntity.builder().build())
                .limit(numberOfOptions)
                .toList();
        Mockito.when(deckOptionRepository.findAllByDeckIdOrderByRowAscIndexAsc(ArgumentMatchers.eq(deckId)))
                .thenReturn(Flux.fromIterable(options));
        Mockito.when(deckOptionMapper.toBO(ArgumentMatchers.any(DeckOptionEntity.class)))
                .thenReturn(DeckOptionBO.builder().build());
        StepVerifier.create(deckOptionService.findAllForDeck(deckId))
                .expectSubscription()
                .expectNextCount(numberOfOptions)
                .verifyComplete();
    }

    @Test
    void testFindSingleById() {
        final Long optionId = RandomUtils.nextLong();
        final DeckOptionEntity option = DeckOptionEntity.builder().build();
        Mockito.when(deckOptionRepository.findById(ArgumentMatchers.eq(optionId)))
                .thenReturn(Mono.just(option));
        final DeckOptionBO expected = DeckOptionBO.builder().build();
        Mockito.when(deckOptionMapper.toBO(ArgumentMatchers.eq(option)))
                .thenReturn(expected);
        StepVerifier.create(deckOptionService.findById(optionId))
                .expectSubscription()
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void testFindManyById() {
        final List<Long> optionIds = Stream.generate(RandomUtils::nextLong).limit(RandomUtils.nextLong(1, 10)).toList();
        final List<DeckOptionEntity> options = Stream.generate(() -> DeckOptionEntity.builder().build())
                .limit(optionIds.size())
                .toList();
        Mockito.when(deckOptionRepository.findAllById(ArgumentMatchers.eq(optionIds)))
                .thenReturn(Flux.fromIterable(options));
        Mockito.when(deckOptionMapper.toBO(ArgumentMatchers.any(DeckOptionEntity.class)))
                .thenReturn(DeckOptionBO.builder().build());
        StepVerifier.create(deckOptionService.findById(optionIds))
                .expectSubscription()
                .expectNextCount(optionIds.size())
                .verifyComplete();
    }

}
