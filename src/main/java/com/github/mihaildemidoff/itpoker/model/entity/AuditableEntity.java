package com.github.mihaildemidoff.itpoker.model.entity;

import java.time.LocalDateTime;

public interface AuditableEntity {

    LocalDateTime getCreatedDate();

    LocalDateTime getModifiedDate();
}

