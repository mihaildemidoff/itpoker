package com.github.mihaildemidoff.itpoker.model.entity;

import com.github.mihaildemidoff.itpoker.model.common.DeckType;
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

@Table("deck")
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@Builder(toBuilder = true)
public class DeckEntity implements AuditableEntity {
    @NotNull
    @Id
    private final Long id;
    @NotNull
    @Column("type")
    private final DeckType type;
    @NotNull
    @Column("title")
    private final String title;
    @NotNull
    @Column("description")
    private final String description;
    @NotNull
    @Column("created_date")
    @CreatedDate
    private final LocalDateTime createdDate;
    @NotNull
    @Column("modified_date")
    @LastModifiedDate
    private final LocalDateTime modifiedDate;
}
