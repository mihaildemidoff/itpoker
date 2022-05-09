package com.github.mihaildemidoff.itpoker.mapper;

import com.github.mihaildemidoff.itpoker.model.bo.PollBO;
import com.github.mihaildemidoff.itpoker.model.common.PollStatus;
import com.github.mihaildemidoff.itpoker.model.common.ProcessingStatus;
import com.github.mihaildemidoff.itpoker.model.entity.PollEntity;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class PollMapperTest {

    @InjectMocks
    private ServicePollMapperImpl mapper;

    @Test
    void testToBO() {
        final PollEntity source = PollEntity.builder()
                .id(RandomUtils.nextLong())
                .deckId(RandomUtils.nextLong())
                .status(PollStatus.values()[RandomUtils.nextInt(0, PollStatus.values().length)])
                .messageId(RandomStringUtils.randomAlphabetic(10))
                .authorId(RandomUtils.nextLong())
                .query(RandomStringUtils.randomAlphabetic(10))
                .needRefresh(RandomUtils.nextBoolean())
                .processingStatus(ProcessingStatus.values()[RandomUtils.nextInt(0, ProcessingStatus.values().length)])
                .createdDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(RandomUtils.nextLong()), ZoneId.systemDefault()))
                .modifiedDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(RandomUtils.nextLong()), ZoneId.systemDefault()))
                .build();
        final PollBO result = mapper.toBO(source);
        assertThat(result.id(), CoreMatchers.is(source.getId()));
        assertThat(result.deckId(), CoreMatchers.is(source.getDeckId()));
        assertThat(result.status(), CoreMatchers.is(source.getStatus()));
        assertThat(result.messageId(), CoreMatchers.is(source.getMessageId()));
        assertThat(result.authorId(), CoreMatchers.is(source.getAuthorId()));
        assertThat(result.query(), CoreMatchers.is(source.getQuery()));
        assertThat(result.needRefresh(), CoreMatchers.is(source.getNeedRefresh()));
        assertThat(result.processingStatus(), CoreMatchers.is(source.getProcessingStatus()));
        assertThat(result.createdDate(), CoreMatchers.is(source.getCreatedDate()));
        assertThat(result.modifiedDate(), CoreMatchers.is(source.getModifiedDate()));
    }

    @Test
    void testToBONullSource() {
        assertThat(mapper.toBO(null), CoreMatchers.nullValue());
    }

}
