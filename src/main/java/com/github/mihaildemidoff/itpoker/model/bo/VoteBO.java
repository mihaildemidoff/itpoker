package com.github.mihaildemidoff.itpoker.model.bo;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record VoteBO(Long id,
                     Long pollId,
                     Long deckOptionId,
                     Long userId,
                     String username,
                     String firstName,
                     String lastName,
                     LocalDateTime createdDate,
                     LocalDateTime modifiedDate) {
}
