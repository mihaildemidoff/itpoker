package com.github.mihaildemidoff.itpoker.service.telegram;

import com.github.mihaildemidoff.itpoker.model.bo.DeckOptionBO;
import com.github.mihaildemidoff.itpoker.model.bo.VoteBO;
import com.github.mihaildemidoff.itpoker.model.bo.template.PollTemplateBO;
import com.github.mihaildemidoff.itpoker.model.bo.template.VoteTemplateBO;
import com.github.mihaildemidoff.itpoker.model.common.PollStatus;
import com.github.mihaildemidoff.itpoker.service.deck.DeckOptionService;
import com.github.mihaildemidoff.itpoker.service.poll.PollService;
import com.github.mihaildemidoff.itpoker.service.vote.VoteService;
import com.google.common.collect.MoreCollectors;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.Writer;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService {

    private final PollService pollService;
    private final VoteService voteService;
    private final DeckOptionService deckOptionService;
    private final Configuration configuration;

    public Mono<String> generateVoteTemplate(final PollTemplateBO pollTemplateBO) {
        return Mono.just(pollTemplateBO)
                .flatMap(args -> Mono.fromCallable(() -> {
                    final Template template = configuration.getTemplate("poll.ftl");
                    try (final StringWriter stringWriter = new StringWriter();
                         final Writer out = new BufferedWriter(stringWriter)) {
                        template.process(args, out);
                        return stringWriter.toString();
                    }
                }));
    }

    public Mono<String> generateTemplateForPoll(final String messageId) {
        return pollService.findPollByMessageId(messageId)
                .flatMap(poll -> getVotes(poll.id())
                        .collectList()
                        .map(votes -> PollTemplateBO.builder()
                                .finished(poll.status() == PollStatus.FINISHED)
                                .taskName(poll.query())
                                .consensus(votes.stream().map(VoteTemplateBO::getValue).distinct().count() == 1)
                                .votes(votes)
                                .build())
                        .flatMap(this::generateVoteTemplate));
    }

    private Flux<VoteTemplateBO> getVotes(final Long pollId) {
        return voteService.findVotesByPollId(pollId)
                .collectList()
                .flatMapMany(votes -> deckOptionService
                        .findById(votes.stream()
                                .map(VoteBO::deckOptionId)
                                .toList())
                        .collectList()
                        .flatMapIterable(options -> votes.stream()
                                .map(vote -> VoteTemplateBO.builder()
                                        .userId(vote.userId().toString())
                                        .firstName(vote.firstName())
                                        .lastName(vote.lastName())
                                        .value(options.stream()
                                                .filter(option -> option.id().equals(vote.deckOptionId()))
                                                .collect(MoreCollectors.toOptional())
                                                .map(DeckOptionBO::text)
                                                .orElse(null))
                                        .build()).toList()));
    }

}
