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

@Table("vote")
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@Builder(toBuilder = true)
public class VoteEntity implements AuditableEntity {
    @NotNull
    @Id
    private final Long id;
    @NotNull
    @Column("poll_id")
    private final Long pollId;
    @NotNull
    @Column("deck_option_id")
    private final Long deckOptionId;
    @NotNull
    @Column("user_id")
    private final Long userId;
    @Column("username")
    private final String username;
    @Column("first_name")
    private final String firstName;
    @Column("last_name")
    private final String lastName;
    @NotNull
    @Column("created_date")
    @CreatedDate
    private final LocalDateTime createdDate;
    @NotNull
    @Column("modified_date")
    @LastModifiedDate
    private final LocalDateTime modifiedDate;
}
