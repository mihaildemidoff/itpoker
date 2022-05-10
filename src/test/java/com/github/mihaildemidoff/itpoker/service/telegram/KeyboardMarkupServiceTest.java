package com.github.mihaildemidoff.itpoker.service.telegram;

import com.github.mihaildemidoff.itpoker.model.bo.ButtonType;
import com.github.mihaildemidoff.itpoker.model.bo.DeckOptionBO;
import com.github.mihaildemidoff.itpoker.service.deck.DeckOptionService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class KeyboardMarkupServiceTest {
    @InjectMocks
    private KeyboardMarkupService keyboardMarkupService;
    @Mock
    private DeckOptionService deckOptionService;

    @ParameterizedTest
    @MethodSource("buildMarkupData")
    void testBuildMarkup(final List<DeckOptionBO> deckOptions,
                         final List<ButtonType> includedButtonTypes,
                         final InlineKeyboardMarkup expectedMarkup) {
        final Long deckId = RandomUtils.nextLong();
        Mockito.lenient().when(deckOptionService.findAllForDeck(ArgumentMatchers.eq(deckId)))
                .thenReturn(Flux.fromIterable(deckOptions));
        StepVerifier.create(keyboardMarkupService.buildMarkup(deckId, includedButtonTypes))
                .expectSubscription()
                .expectNext(expectedMarkup)
                .verifyComplete();
    }

    private static DeckOptionBO option(final Long id,
                                       final String text,
                                       final Integer row,
                                       final Integer index) {
        return DeckOptionBO.builder()
                .id(id)
                .text(text)
                .row(row)
                .index(index)
                .build();
    }

    private static InlineKeyboardButton button(final String text,
                                               final String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }


    @ParameterizedTest
    @MethodSource("getButtonTypeData")
    void testGetButtonType(final String callbackData,
                           final ButtonType buttonType) {
        StepVerifier.create(keyboardMarkupService.getButtonType(callbackData))
                .expectSubscription()
                .expectNext(buttonType)
                .verifyComplete();
    }

    public static Stream<Arguments> buildMarkupData() {
        return Stream.of(
                Arguments.of(
                        List.of(option(1L, "1", 0, 0), option(2L, "2", 0, 1), option(3L, "3", 1, 0)),
                        List.of(ButtonType.VOTE, ButtonType.RESTART, ButtonType.FINISH),
                        InlineKeyboardMarkup.builder()
                                .keyboard(List.of(List.of(button("1", "1"), button("2", "2")), List.of(button("3", "3")), List.of(button("Finish", "Finish"), button("Restart", "Restart"))))
                                .build()
                ),
                Arguments.of(
                        List.of(option(1L, "1", 0, 0), option(2L, "2", 0, 1), option(3L, "3", 1, 0)),
                        List.of(ButtonType.RESTART),
                        InlineKeyboardMarkup.builder()
                                .keyboard(List.of(List.of(button("Restart", "Restart"))))
                                .build()
                )
        );
    }

    private static Stream<Arguments> getButtonTypeData() {
        return Stream.of(
                Arguments.of(RandomStringUtils.randomAlphabetic(10), ButtonType.VOTE),
                Arguments.of("Finish", ButtonType.FINISH),
                Arguments.of("Restart", ButtonType.RESTART)
        );
    }

}
