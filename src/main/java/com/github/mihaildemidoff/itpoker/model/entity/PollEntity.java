package com.github.mihaildemidoff.itpoker.model.entity;

import com.github.mihaildemidoff.itpoker.model.common.PollStatus;
import com.github.mihaildemidoff.itpoker.model.common.ProcessingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Table("poll")
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@Builder(toBuilder = true)
public class PollEntity implements AuditableEntity {
    @Id
    @NotNull
    private final Long id;
    @NotNull
    @Column("deck_id")
    private final Long deckId;
    @NotNull
    @Column("status")
    private final PollStatus status;
    @NotNull
    @Column("message_id")
    private final String messageId;
    @NotNull
    @Column("author_id")
    private final Long authorId;
    @NotNull
    @Column("query")
    private final String query;
    @NotNull
    @Column("need_refresh")
    private final Boolean needRefresh;
    @NotNull
    @Column("processing_status")
    private final ProcessingStatus processingStatus;
    @Column("created_date")
    @CreatedDate
    private final LocalDateTime createdDate;
    @Column("modified_date")
    @LastModifiedDate
    private final LocalDateTime modifiedDate;
}
