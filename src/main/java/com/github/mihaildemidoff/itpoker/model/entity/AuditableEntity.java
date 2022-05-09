package com.github.mihaildemidoff.itpoker.model.entity;

import java.time.LocalDateTime;

public interface AuditableEntity {
    void setCreatedDate(LocalDateTime createdDate);

    LocalDateTime getCreatedDate();

    void setModifiedDate(LocalDateTime modifiedDate);

    LocalDateTime getModifiedDate();
}

