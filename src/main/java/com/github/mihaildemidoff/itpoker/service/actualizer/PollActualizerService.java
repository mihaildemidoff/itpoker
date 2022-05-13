package com.github.mihaildemidoff.itpoker.service.actualizer;

import com.github.mihaildemidoff.itpoker.model.bo.ButtonType;
import com.github.mihaildemidoff.itpoker.model.bo.PollBO;
import com.github.mihaildemidoff.itpoker.model.common.PollStatus;
import com.github.mihaildemidoff.itpoker.service.poll.PollService;
import com.github.mihaildemidoff.itpoker.service.telegram.KeyboardMarkupService;
import com.github.mihaildemidoff.itpoker.service.telegram.SenderHelper;
import com.github.mihaildemidoff.itpoker.service.telegram.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PollActualizerService {
    private final PollService pollService;
    private final AbsSender absSender;
    private final TemplateService templateService;
    private final KeyboardMarkupService keyboardMarkupService;
    private final SenderHelper senderHelper;

    public Flux<Long> getStuckUpdaterChain() {
        return Flux.interval(Duration.ofSeconds(3))
                .flatMap(i -> pollService.switchStuckRequestsToReadyToProcess(Duration.ofSeconds(5)))
                .doOnNext(count -> log.info("Returned to READY_TO_PROCESS " + count + " polls"));
    }

    public Flux<Boolean> actualizePolls() {
        return Flux.interval(Duration.ofSeconds(1L))
                .flatMap(index -> pollService.findNextPollForProcessing())
                .flatMap(poll -> Mono.zip(templateService.generateTemplateForPoll(poll.messageId()),
                                keyboardMarkupService.buildMarkup(poll.deckId(), poll.status() == PollStatus.FINISHED ? List.of(ButtonType.RESTART) : List.of(ButtonType.VOTE, ButtonType.RESTART, ButtonType.FINISH)))
                        .map(t -> buildMessage(poll, t.getT1(), t.getT2()))
                        .flatMap(message -> senderHelper.executeAsync(absSender, message))
                        .flatMap(serializable -> pollService.moveToReadyToProcess(poll.id()))
                        .onErrorResume(e -> pollService.moveToReadyToProcess(poll.id()))
                        .thenReturn(true))
                .onErrorContinue((e, o) -> {
                    log.error("Error occurred during getting poll from db", e);
                });
    }

    private EditMessageText buildMessage(final PollBO poll,
                                         final String template,
                                         final InlineKeyboardMarkup markup) {
        final EditMessageText editMessageText = new EditMessageText();
        editMessageText.setInlineMessageId(poll.messageId());
        editMessageText.setReplyMarkup(markup);
        editMessageText.setParseMode("HTML");
        editMessageText.setDisableWebPagePreview(true);
        editMessageText.setText(template);
        return editMessageText;
    }
}


