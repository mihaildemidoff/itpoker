package com.github.mihaildemidoff.itpoker.model.entity;

import com.github.mihaildemidoff.itpoker.model.common.DeckType;
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

@Table("deck")
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@Builder(toBuilder = true)
public class DeckEntity implements AuditableEntity {
    @Id
    private final Long id;
    @Column("type")
    private final DeckType type;
    @Column("title")
    private final String title;
    @Column("description")
    private final String description;
    @Column("created_date")
    @CreatedDate
    private final LocalDateTime createdDate;
    @Column("modified_date")
    @LastModifiedDate
    private final LocalDateTime modifiedDate;
}
