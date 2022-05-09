package com.github.mihaildemidoff.itpoker.model.bo.template;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public final class VoteTemplateBO {
    private final String userId;
    private final String firstName;
    private final String lastName;
    private final String value;
}
