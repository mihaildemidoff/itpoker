package com.github.mihaildemidoff.itpoker.model.entity;

import com.github.mihaildemidoff.itpoker.model.common.PollStatus;
import com.github.mihaildemidoff.itpoker.model.common.ProcessingStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Table("poll")
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@With
public class PollEntity implements AuditableEntity {
    @Id
    @NotNull
    private Long id;
    @NotNull
    @Column("deck_id")
    private Long deckId;
    @NotNull
    @Column("status")
    private PollStatus status = PollStatus.IN_PROGRESS;
    @NotNull
    @Column("message_id")
    private String messageId;
    @NotNull
    @Column("author_id")
    private Long authorId;
    @NotNull
    @Column("query")
    private String query;
    @NotNull
    @Column("need_refresh")
    private Boolean needRefresh;
    @NotNull
    @Column("processing_status")
    private ProcessingStatus processingStatus;
    @Column("created_date")
    @CreatedDate
    private LocalDateTime createdDate;
    @Column("modified_date")
    @LastModifiedDate
    private LocalDateTime modifiedDate;
}
