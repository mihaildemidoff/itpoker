package com.github.mihaildemidoff.itpoker.model.bo;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record DeckOptionBO(Long id,
                           Long deckId,
                           String text,
                           Integer row,
                           Integer index,
                           LocalDateTime createdDate,
                           LocalDateTime modifiedDate) {

}
