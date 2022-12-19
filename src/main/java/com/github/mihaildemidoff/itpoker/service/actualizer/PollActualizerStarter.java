package com.github.mihaildemidoff.itpoker.service.actualizer;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;


@Service
@RequiredArgsConstructor
@Slf4j
public class PollActualizerStarter {
    private final PollActualizerService pollActualizerService;

    private Disposable pollActualizerChain;
    private Disposable pollStuckedRequestsChain;

    @EventListener
    public void onApplicationEvent(final ApplicationReadyEvent applicationReadyEvent) {
        pollActualizerChain = pollActualizerService.actualizePolls().subscribe();
        pollStuckedRequestsChain = pollActualizerService.getStuckUpdaterChain().subscribe();
    }

    @PreDestroy
    public void onDestroy() {
        if (pollActualizerChain != null) {
            pollActualizerChain.dispose();
        }
        if (pollStuckedRequestsChain != null) {
            pollStuckedRequestsChain.dispose();
        }
    }

}
