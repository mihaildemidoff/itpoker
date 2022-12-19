package com.github.mihaildemidoff.itpoker.model.entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("deck_option")
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@Builder(toBuilder = true)
public class DeckOptionEntity implements AuditableEntity {
    @NotNull
    @Id
    private final Long id;
    @NotNull
    @Column("deck_id")
    private final Long deckId;
    @NotNull
    @Column("text")
    private final String text;
    @NotNull
    @Column("row")
    private final Integer row;
    @NotNull
    @Column("index")
    private final Integer index;
    @NotNull
    @Column("created_date")
    @CreatedDate
    private final LocalDateTime createdDate;
    @NotNull
    @Column("modified_date")
    @LastModifiedDate
    private final LocalDateTime modifiedDate;
}
