package com.github.mihaildemidoff.itpoker.model.bo.template;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Builder(toBuilder = true)
@EqualsAndHashCode
@Getter
public final class PollTemplateBO {
    private final String taskName;
    private final boolean finished;
    private final boolean hasDecision;
    private final String decision;
    private final List<VoteTemplateBO> votes;
}
