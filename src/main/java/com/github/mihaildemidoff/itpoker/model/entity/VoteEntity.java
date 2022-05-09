package com.github.mihaildemidoff.itpoker.model.entity;

import com.github.mihaildemidoff.itpoker.model.entity.AuditableEntity;
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

@Table("vote")
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@With
public class VoteEntity implements AuditableEntity {
    @Id
    private Long id;
    @Column("poll_id")
    private Long pollId;
    @Column("deck_option_id")
    private Long deckOptionId;
    @Column("user_id")
    private Long userId;
    @Column("username")
    private String username;
    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    @Column("created_date")
    @CreatedDate
    private LocalDateTime createdDate;
    @Column("modified_date")
    @LastModifiedDate
    private LocalDateTime modifiedDate;
}
