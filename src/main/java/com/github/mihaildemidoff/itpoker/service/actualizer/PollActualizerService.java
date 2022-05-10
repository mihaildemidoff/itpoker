package com.github.mihaildemidoff.itpoker.service.actualizer;

import com.github.mihaildemidoff.itpoker.model.bo.ButtonType;
import com.github.mihaildemidoff.itpoker.model.common.PollStatus;
import com.github.mihaildemidoff.itpoker.service.poll.PollService;
import com.github.mihaildemidoff.itpoker.service.telegram.KeyboardMarkupService;
import com.github.mihaildemidoff.itpoker.service.telegram.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.bots.AbsSender;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class PollActualizerService {
    private final PollService pollService;
    private final AbsSender absSender;
    private final TemplateService templateService;
    private final KeyboardMarkupService keyboardMarkupService;

    public Flux<Boolean> actualizePolls() {
        return Flux.interval(Duration.ofSeconds(1L))
                .flatMap(index -> pollService.findNextPollForProcessing())
                .flatMap(poll -> {
                    return templateService.generateTemplateForPoll(poll.messageId())
                            .flatMap(template -> {
                                return keyboardMarkupService.buildMarkup(poll.deckId(), poll.status() == PollStatus.FINISHED ? List.of(ButtonType.RESTART) : List.of(ButtonType.VOTE, ButtonType.RESTART, ButtonType.FINISH))
                                        .map(markup -> {
                                            final EditMessageText editMessageText = new EditMessageText();
                                            editMessageText.setInlineMessageId(poll.messageId());
                                            editMessageText.setReplyMarkup(markup);
                                            editMessageText.setParseMode("HTML");
                                            editMessageText.setDisableWebPagePreview(true);
                                            editMessageText.setText(template);
                                            return editMessageText;
                                        })
                                        .flatMap(new Function<EditMessageText, Mono<? extends Serializable>>() {
                                            @Override
                                            @SneakyThrows
                                            public Mono<? extends Serializable> apply(final EditMessageText message) {
                                                return Mono.fromFuture(absSender.executeAsync(message));
                                            }
                                        })
                                        .flatMap(serializable -> pollService.moveToReadyToProcess(poll.id()))
                                        .onErrorResume(e -> pollService.moveToReadyToProcess(poll.id()))
                                        .thenReturn(true);
                            });
                })
                .onErrorContinue((e, o) -> {
                    log.error("Error occured during getting poll from db", e);
                });
    }
}


