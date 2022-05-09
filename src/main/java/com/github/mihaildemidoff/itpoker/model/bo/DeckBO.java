package com.github.mihaildemidoff.itpoker.model.bo;

import com.github.mihaildemidoff.itpoker.model.common.DeckType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record DeckBO(Long id,
                     DeckType type,
                     String title,
                     String description,
                     LocalDateTime createdDate,
                     LocalDateTime modifiedDate) {

}
