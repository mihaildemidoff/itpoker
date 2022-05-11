package com.github.mihaildemidoff.itpoker.service.telegram;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.test.StepVerifier;

import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class SenderHelperTest {

    @InjectMocks
    private SenderHelper senderHelper;
    @Mock
    private AbsSender absSender;
    @Mock
    private BotApiMethod<Boolean> method;

    @Test
    void testExecuteAsync() throws Exception {
        Mockito.when(absSender.executeAsync(ArgumentMatchers.eq(method)))
                .thenReturn(CompletableFuture.supplyAsync(() -> true));
        StepVerifier.create(senderHelper.executeAsync(absSender, method))
                .expectSubscription()
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void testExecuteAsyncError() throws Exception {
        Mockito.when(absSender.executeAsync(ArgumentMatchers.eq(method)))
                .thenThrow(TelegramApiException.class);
        StepVerifier.create(senderHelper.executeAsync(absSender, method))
                .expectSubscription()
                .verifyError(TelegramApiException.class);
    }

}
