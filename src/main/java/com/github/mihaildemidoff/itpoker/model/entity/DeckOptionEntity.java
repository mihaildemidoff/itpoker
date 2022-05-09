package com.github.mihaildemidoff.itpoker.model.entity;

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

import java.time.LocalDateTime;

@Table("deck_option")
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@With
public class DeckOptionEntity implements AuditableEntity {
    @Id
    private Long id;
    @Column("deck_id")
    private Long deckId;
    @Column("text")
    private String text;
    @Column("row")
    private Integer row;
    @Column("index")
    private Integer index;
    @Column("created_date")
    @CreatedDate
    private LocalDateTime createdDate;
    @Column("modified_date")
    @LastModifiedDate
    private LocalDateTime modifiedDate;
}
