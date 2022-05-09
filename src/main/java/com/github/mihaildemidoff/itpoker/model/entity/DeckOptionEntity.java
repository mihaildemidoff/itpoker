package com.github.mihaildemidoff.itpoker.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
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

import java.time.LocalDateTime;

@Table("deck_option")
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@Builder(toBuilder = true)
public class DeckOptionEntity implements AuditableEntity {
    @Id
    private final Long id;
    @Column("deck_id")
    private final Long deckId;
    @Column("text")
    private final String text;
    @Column("row")
    private final Integer row;
    @Column("index")
    private final Integer index;
    @Column("created_date")
    @CreatedDate
    private final LocalDateTime createdDate;
    @Column("modified_date")
    @LastModifiedDate
    private final LocalDateTime modifiedDate;
}
