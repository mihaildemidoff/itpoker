package com.github.mihaildemidoff.itpoker.model.entity;

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
    @Id
    private final Long id;
    @Column("poll_id")
    private final Long pollId;
    @Column("deck_option_id")
    private final Long deckOptionId;
    @Column("user_id")
    private final Long userId;
    @Column("username")
    private final String username;
    @Column("first_name")
    private final String firstName;
    @Column("last_name")
    private final String lastName;
    @Column("created_date")
    @CreatedDate
    private final LocalDateTime createdDate;
    @Column("modified_date")
    @LastModifiedDate
    private final LocalDateTime modifiedDate;
}
