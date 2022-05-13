package com.github.mihaildemidoff.itpoker.service.actualizer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.publisher.PublisherProbe;

@ExtendWith(MockitoExtension.class)
class PollActualizerStarterTest {

    @InjectMocks
    private PollActualizerStarter starter;
    @Mock
    private PollActualizerService pollActualizerService;

    @Test
    void testStart() {
        PublisherProbe<Boolean> pollActualizerProbe =
                PublisherProbe.of(Flux.just(true));
        PublisherProbe<Long> stuckedRequestsProbe =
                PublisherProbe.of(Flux.just(1L));
        Mockito.when(pollActualizerService.actualizePolls())
                .thenReturn(pollActualizerProbe.flux());
        Mockito.when(pollActualizerService.getStuckUpdaterChain())
                .thenReturn(stuckedRequestsProbe.flux());
        starter.onApplicationEvent(null);
        pollActualizerProbe.assertWasSubscribed();
        stuckedRequestsProbe.assertWasSubscribed();
    }

}
