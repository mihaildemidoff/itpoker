package com.github.mihaildemidoff.itpoker.service.telegram;

import com.github.mihaildemidoff.itpoker.model.bo.ButtonType;
import com.github.mihaildemidoff.itpoker.model.bo.DeckOptionBO;
import com.github.mihaildemidoff.itpoker.service.deck.DeckOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class KeyboardMarkupService {
    private static final InlineKeyboardButton FINISH_BUTTON = InlineKeyboardButton.builder()
            .text("Finish")
            .callbackData("Finish")
            .build();
    private static final InlineKeyboardButton RESTART_BUTTON = InlineKeyboardButton.builder()
            .text("Restart")
            .callbackData("Restart")
            .build();
    private final DeckOptionService deckOptionService;

    @Transactional
    public Mono<InlineKeyboardMarkup> buildMarkup(final Long deckId) {
        return deckOptionService.findAllForDeck(deckId)
                .collectList()
                .map(entities -> entities.stream().collect(Collectors.groupingBy(DeckOptionBO::row)))
                .map(grouped -> {
                    final List<List<InlineKeyboardButton>> buttons = Stream.concat(grouped.keySet()
                                            .stream()
                                            .sorted()
                                            .map(row -> grouped.get(row)
                                                    .stream()
                                                    .map(option -> InlineKeyboardButton.builder()
                                                            .text(option.text())
                                                            .callbackData(option.id().toString())
                                                            .build())
                                                    .toList()),
                                    Stream.of(List.of(FINISH_BUTTON, RESTART_BUTTON))
                            )
                            .toList();
                    return InlineKeyboardMarkup.builder().keyboard(buttons).build();
                });
    }

    public Mono<ButtonType> getButtonType(final String callbackData) {
        return Mono.just(callbackData)
                .map(data -> {
                    if (Objects.equals(callbackData, FINISH_BUTTON.getCallbackData())) {
                        return ButtonType.FINISH;
                    } else if (Objects.equals(callbackData, RESTART_BUTTON.getCallbackData())) {
                        return ButtonType.RESTART;
                    }
                    return ButtonType.VOTE;
                });
    }

}
