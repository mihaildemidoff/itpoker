package com.github.mihaildemidoff.itpoker.mapper;

import com.github.mihaildemidoff.itpoker.model.bo.VoteBO;
import com.github.mihaildemidoff.itpoker.model.entity.VoteEntity;
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
class VoteMapperTest {

    @InjectMocks
    private ServiceVoteMapperImpl mapper;

    @Test
    void testToBO() {
        final VoteEntity source = VoteEntity.builder()
                .id(RandomUtils.nextLong())
                .pollId(RandomUtils.nextLong())
                .deckOptionId(RandomUtils.nextLong())
                .userId(RandomUtils.nextLong())
                .username(RandomStringUtils.randomAlphabetic(10))
                .firstName(RandomStringUtils.randomAlphabetic(10))
                .lastName(RandomStringUtils.randomAlphabetic(10))
                .createdDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(RandomUtils.nextLong()), ZoneId.systemDefault()))
                .modifiedDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(RandomUtils.nextLong()), ZoneId.systemDefault()))
                .build();
        final VoteBO result = mapper.toBO(source);
        assertThat(result.id(), CoreMatchers.is(source.getId()));
        assertThat(result.pollId(), CoreMatchers.is(source.getPollId()));
        assertThat(result.deckOptionId(), CoreMatchers.is(source.getDeckOptionId()));
        assertThat(result.userId(), CoreMatchers.is(source.getUserId()));
        assertThat(result.username(), CoreMatchers.is(source.getUsername()));
        assertThat(result.firstName(), CoreMatchers.is(source.getFirstName()));
        assertThat(result.lastName(), CoreMatchers.is(source.getLastName()));
        assertThat(result.createdDate(), CoreMatchers.is(source.getCreatedDate()));
        assertThat(result.modifiedDate(), CoreMatchers.is(source.getModifiedDate()));
    }

    @Test
    void testToBONullSource() {
        assertThat(mapper.toBO(null), CoreMatchers.nullValue());
    }
}
