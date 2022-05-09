package com.github.mihaildemidoff.itpoker.model.bo;

import com.github.mihaildemidoff.itpoker.model.common.PollStatus;
import com.github.mihaildemidoff.itpoker.model.common.ProcessingStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record PollBO(Long id,
                     Long deckId,
                     PollStatus status,
                     String messageId,
                     Long authorId,
                     String query,
                     Boolean needRefresh,
                     ProcessingStatus processingStatus,
                     LocalDateTime createdDate,
                     LocalDateTime modifiedDate) {
}
