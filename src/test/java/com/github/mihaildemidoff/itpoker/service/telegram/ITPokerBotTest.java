package com.github.mihaildemidoff.itpoker.service.telegram;

import com.github.mihaildemidoff.itpoker.service.telegram.handler.UpdateHandler;
import io.github.mihaildemidoff.reactive.tg.bots.core.client.api.TelegramPoller;
import io.github.mihaildemidoff.reactive.tg.bots.model.update.Update;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class ITPokerBotTest {

    private ITPokerBot itPokerBot;
    @Mock
    private TelegramPoller telegramPoller;
    @Mock
    private UpdateHandler firstHandler;
    @Mock
    private UpdateHandler secondHandler;

    @BeforeEach
    void setUp() {
        itPokerBot = new ITPokerBot(telegramPoller, List.of(firstHandler, secondHandler));
    }

    @Test
    void testProcessedBySeveralHandlers() {
        // start processing
        final long erroredNumberOfUpdates = RandomUtils.nextLong(10, 100);
        final List<Update> erroredUpdates = Stream.generate(() -> Mockito.mock(Update.class))
                .limit(erroredNumberOfUpdates)
                .toList();
        final long firstHandlerNumberOfUpdates = RandomUtils.nextLong(10, 100);
        final List<Update> firstHandlerUpdates = Stream.generate(() -> Mockito.mock(Update.class))
                .limit(firstHandlerNumberOfUpdates)
                .toList();
        final long secondHandlerNumberOfUpdates = RandomUtils.nextLong(10, 100);
        final List<Update> secondHandlerUpdates = Stream.generate(() -> Mockito.mock(Update.class))
                .limit(secondHandlerNumberOfUpdates)
                .toList();
        final long numberOfUpdatesWithoutHandler = RandomUtils.nextLong(10, 100);
        final List<Update> updatesWithoutHandler = Stream.generate(() -> Mockito.mock(Update.class))
                .limit(numberOfUpdatesWithoutHandler)
                .toList();
        Mockito.when(firstHandler.shouldHandle(ArgumentMatchers.any(Update.class)))
                .thenAnswer((Answer<Boolean>) invocationOnMock -> firstHandlerUpdates.contains(invocationOnMock.getArgument(0)));
        Mockito.when(secondHandler.shouldHandle(ArgumentMatchers.any(Update.class)))
                .thenAnswer((Answer<Boolean>) invocationOnMock -> secondHandlerUpdates.contains(invocationOnMock.getArgument(0)) || erroredUpdates.contains(invocationOnMock.getArgument(0)));
        Mockito.when(firstHandler.handle(ArgumentMatchers.any(Update.class)))
                .thenReturn(Mono.just(true));
        Mockito.when(secondHandler.handle(ArgumentMatchers.any(Update.class)))
                .thenAnswer((Answer<Mono<Boolean>>) invocationOnMock -> {
                    if (secondHandlerUpdates.contains(invocationOnMock.getArgument(0))) {
                        return Mono.just(true);
                    } else {
                        return Mono.error(IllegalArgumentException::new);
                    }
                });
        final List<Update> allUpdates = Stream.concat(Stream.concat(firstHandlerUpdates.stream(), secondHandlerUpdates.stream()),
                        Stream.concat(updatesWithoutHandler.stream(), erroredUpdates.stream()))
                .collect(Collectors.toList());
        Collections.shuffle(allUpdates);
        Mockito.when(telegramPoller.getUpdatesPublisher(ArgumentMatchers.eq(List.of())))
                .thenReturn(Flux.fromIterable(allUpdates));
        itPokerBot.onApplicationEvent(null);
        Mockito.verify(firstHandler, Mockito.times((int) firstHandlerNumberOfUpdates))
                .handle(ArgumentMatchers.any(Update.class));
        Mockito.verify(secondHandler, Mockito.times((int) secondHandlerNumberOfUpdates + (int) erroredNumberOfUpdates))
                .handle(ArgumentMatchers.any(Update.class));
    }

    @Test
    void testAllProcessed() {
        // start processing
        Mockito.when(firstHandler.shouldHandle(ArgumentMatchers.any(Update.class)))
                .thenReturn(true);
        final long numberOfUpdates = RandomUtils.nextLong(10, 100);
        final List<Update> updates = Stream.generate(() -> Mockito.mock(Update.class))
                .limit(numberOfUpdates)
                .toList();
        Mockito.when(telegramPoller.getUpdatesPublisher(ArgumentMatchers.eq(List.of())))
                .thenReturn(Flux.fromIterable(updates));
        Mockito.when(firstHandler.handle(ArgumentMatchers.any(Update.class)))
                .thenReturn(Mono.just(true));
        itPokerBot.onApplicationEvent(null);
        Mockito.verify(firstHandler, Mockito.times((int) numberOfUpdates))
                .handle(ArgumentMatchers.any(Update.class));
    }

}
